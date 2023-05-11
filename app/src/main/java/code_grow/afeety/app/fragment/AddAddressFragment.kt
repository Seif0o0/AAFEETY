package code_grow.afeety.app.fragment

import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.databinding.FragmentAddAddressBinding
import code_grow.afeety.app.databinding.SuccessDialogLayoutBinding
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.model.Area
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.AddAddressViewModel
import code_grow.afeety.app.view_model.AddAddressViewModelFactory
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.io.IOException

private const val TAG = "AddAddressFragment"

class AddAddressFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentAddAddressBinding
    private lateinit var viewModel: AddAddressViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        val viewModelFactory = AddAddressViewModelFactory(
            requireActivity().application,
            CartRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[AddAddressViewModel::class.java]
        if (::binding.isInitialized) {
            binding
        } else {
            binding = FragmentAddAddressBinding.inflate(inflater, container, false)
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()
        }


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (!Places.isInitialized()) {
            Places.initialize(
                requireContext(),
                getString(R.string.google_map_api_key_1)
            )
        }

        val autoCompleteLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    Activity.RESULT_OK -> {
                        val place = Autocomplete.getPlaceFromIntent(it.data)
                        binding.searchText.text = place.address ?: "null"
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                place.latLng, 15f
                            )
                        )
                    }
                    AutocompleteActivity.RESULT_ERROR -> {
                        it.data?.let { intent ->
                            val status = Autocomplete.getStatusFromIntent(intent)
                            Log.d(TAG, "AutoComplete error: $status")
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        Log.d(TAG, "AutoComplete Canceled")
                    }

                }
            }

        binding.searchText.setOnClickListener {
            val fields = listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(requireContext())
            autoCompleteLauncher.launch(intent)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.addressContainer.visibility == View.VISIBLE) {
                        mMap.uiSettings.isScrollGesturesEnabled = true
                        binding.addressContainer.visibility = View.GONE
                        binding.confirmButton.visibility = View.VISIBLE
                        binding.searchText.text = ""
                        binding.searchText.visibility = View.VISIBLE
                    } else {
                        isEnabled = false
                        findNavController().popBackStack()
                    }
                }
            })


        binding.navigateBack.setOnClickListener {
            if (binding.addressContainer.visibility == View.VISIBLE) {
                mMap.uiSettings.isScrollGesturesEnabled = true
                binding.addressContainer.visibility = View.GONE
                binding.confirmButton.visibility = View.VISIBLE
                binding.searchText.text = ""
                binding.searchText.visibility = View.VISIBLE
            } else {
                findNavController().popBackStack()
            }
        }
        var latLng: LatLng? = null
        binding.confirmButton.setOnClickListener {
            if (::mMap.isInitialized) {
                latLng = mMap.cameraPosition.target
                getAddress(latLng!!)
            }
        }

        binding.saveButton.setOnClickListener {
            viewModel.saveBtnClicked()
        }

        viewModel.startAddAddress.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.addAddress(latLng!!)
            }
        }

        viewModel.addAddressResponse.observe(viewLifecycleOwner) {
            viewModel.startAddAddress(false)
            if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            } else if (it is Resource.Success<*>) {
                val address = (it as Resource.Success<code_grow.afeety.app.model.Address>).data
                showSuccessDialog(addressId = address.addressId)
            }
        }


        /* observe areas req. response */
        viewModel.areasResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                binding.error = it.message
                binding.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                val areas = it.data as MutableList<Area>
                val areasName = mutableListOf<String>()
                for (city in areas) {
                    areasName.add(city.name)
                }
                binding.autocompleteArea.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.register_dropdown_item,
                        areasName
                    )
                )
            }
        }
        binding.errorView.setOnClickListener {
            binding.errorView.visibility = View.GONE
            binding.cancelAnimation = true
            viewModel.getAreas()
        }

        return binding.root
    }


    private lateinit var mMap: GoogleMap
    override fun onMapReady(map: GoogleMap) {
        mMap = map
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(UserInfo.latitude.toDouble(), UserInfo.longitude.toDouble()), 15f
            )
        )
    }

    private var addresses: List<Address>? = null
    private fun getAddress(latLng: LatLng) {
        val geocoder = Geocoder(requireContext())
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                // hide confirm btn -- show editTexts container -- set addressLine to first editText
                mMap.uiSettings.isScrollGesturesEnabled = false
                binding.confirmButton.visibility = View.GONE
                binding.searchText.visibility = View.GONE
                binding.addressContainer.visibility = View.VISIBLE
                var addressLine = addresses!![0].getAddressLine(0)
                if (addressLine.isNullOrEmpty())
                    addressLine = addresses!![0].getAddressLine(1)
                if (addressLine.isNullOrEmpty()) {
                    CustomDialog.showErrorDialog(
                        context = requireContext(),
                        errorMessage = getString(R.string.picked_location_is_not_supported)
                    )
                    return
                }
                binding.addressEditText.setText(addresses!![0].getAddressLine(0))
            }
        } catch (exception: IOException) {
            CustomDialog.showErrorDialog(
                context = requireContext(),
                errorMessage = getString(R.string.picked_location_is_not_supported)
            )
        }
    }

    private fun showSuccessDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
        addressId:Int
    ) {
        val dialog = MaterialDialog(requireContext(), dialogBehavior).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelable(false)
            customView(
                R.layout.success_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val binding = SuccessDialogLayoutBinding.bind(dialog.getCustomView())
        binding.successMessage.text = getString(R.string.add_address_success_message)

        binding.errorAnimation.setAnimation("success.json")
        binding.errorAnimation.playAnimation()
        binding.errorAnimation.repeatCount = LottieDrawable.INFINITE

        binding.okButton.setOnClickListener {
            binding.errorAnimation.cancelAnimation()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            binding.errorAnimation.cancelAnimation()
            findNavController().navigate(
                AddAddressFragmentDirections.actionAddAddressFragmentToOrderDetailsFragment(
                    medicines = AddressesFragmentArgs.fromBundle(requireArguments()).medicines,
                    addressId = addressId ,
                    addressDetails = this.binding.addressEditText.text.toString()
                )
            )
        }

    }
}