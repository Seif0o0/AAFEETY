package code_grow.afeety.app.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.R
import code_grow.afeety.app.adapter.AddressesAdapter
import code_grow.afeety.app.adapter.OnAddressItemClickListener
import code_grow.afeety.app.databinding.FragmentSendPrescriptionBinding
import code_grow.afeety.app.model.Address
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.SendPrescriptionViewModel
import code_grow.afeety.app.view_model.SendPrescriptionViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import java.io.File

private const val TAG = "SendPrescriptionFragment"

class SendPrescriptionFragment : Fragment() {
    private lateinit var binding: FragmentSendPrescriptionBinding
    private lateinit var viewModel: SendPrescriptionViewModel

    private lateinit var selectedAddress: Address
    private var prescriptionFile: File? = null

    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        val viewModelFactory = SendPrescriptionViewModelFactory(
            requireActivity().application,
            CartRepository()
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[SendPrescriptionViewModel::class.java]

        if (::binding.isInitialized) {
            binding
        } else {
            binding = FragmentSendPrescriptionBinding.inflate(inflater, container, false)
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()

            binding.navigateBack.setOnClickListener {
                requireActivity().finish()
            }

            val prescriptionLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        prescriptionFile = ImagePicker.getFile(it.data)!!
                        binding.prescriptionGroup.visibility = View.VISIBLE
                        binding.uploadPrescriptionTitle.text =
                            getString(R.string.change_prescription)
                        binding.prescription.visibility = View.VISIBLE
                        Glide.with(this)
                            .load(prescriptionFile ?: "")
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(binding.prescription)
                    } else {
                        Log.d(TAG, "PickPic Failed: $it")
                    }
                }

            binding.uploadPrescriptionIcon.setOnClickListener {
                pickPrescription(prescriptionLauncher)
            }

            binding.uploadPrescriptionTitle.setOnClickListener {
                pickPrescription(prescriptionLauncher)
            }

            binding.removePrescription.setOnClickListener {
                prescriptionFile = null
                binding.prescriptionGroup.visibility = View.GONE
                binding.uploadPrescriptionTitle.text = getString(R.string.attach_prescription)
            }


            binding.addNewAddress.setOnClickListener {
                if (prescriptionFile == null) {
                    CustomDialog.showErrorDialog(
                        context = requireContext(),
                        errorMessage = getString(R.string.prescription_required)
                    )
                    return@setOnClickListener
                }

                findNavController().navigate(
                    SendPrescriptionFragmentDirections.actionSendPrescriptionFragmentToPreAddAddressFragment(
                        Uri.fromFile(prescriptionFile).toString()
                    )
                )
            }

            binding.nextButton.setOnClickListener {
                if (prescriptionFile == null) {
                    CustomDialog.showErrorDialog(
                        context = requireContext(),
                        errorMessage = getString(R.string.prescription_required)
                    )
                    return@setOnClickListener
                }

                if (!::selectedAddress.isInitialized) {
                    CustomDialog.showErrorDialog(
                        context = requireContext(),
                        errorMessage = getString(R.string.delivery_address_required)
                    )
                    return@setOnClickListener
                }


                findNavController().navigate(
                    SendPrescriptionFragmentDirections.actionSendPrescriptionFragmentToPreOrderDetailsFragment(
                        prescription = Uri.fromFile(prescriptionFile).toString(),
                        addressId = selectedAddress.addressId,
                        addressDetails = selectedAddress.details
                    )
                )
            }

            val addressesAdapter =
                AddressesAdapter(OnAddressItemClickListener { selectedAddress, position ->
                    val adapter = binding.list.adapter as AddressesAdapter
                    val addressesList = adapter.currentList
                    val prevSelectedAddress = addressesList.find {
                        it.isClicked
                    }

                    val prevSelectedAddressIndex = addressesList.indexOf(prevSelectedAddress)
                    prevSelectedAddress?.isClicked = false

                    addressesList[position].isClicked = true

                    adapter.submitList(addressesList)
                    adapter.notifyItemChanged(prevSelectedAddressIndex, Unit)
                    adapter.notifyItemChanged(position, Unit)
                    this.selectedAddress = selectedAddress

                })
            binding.list.adapter = addressesAdapter
            binding.list.layoutManager = LinearLayoutManager(requireContext())

            viewModel.startRequestAddresses.observe(viewLifecycleOwner) {
                if (it) {
                    viewModel.getAddresses()
                }
            }

            viewModel.addressesResponse.observe(viewLifecycleOwner) {
                viewModel.startRequestAddresses(false)
                if (it is Resource.Failed) {
                    binding.errorView.error = it.message
                    binding.errorView.cancelAnimation = false
                } else if (it is Resource.Success<*>) {
                    addressesAdapter.submitList(it.data as MutableList<Address>)
                }
            }

            binding.errorView.root.setOnClickListener {
                binding.errorView.root.visibility = View.GONE
                binding.errorView.cancelAnimation = true
                viewModel.startRequestAddresses(true)
            }
        }
        return binding.root
    }

    private fun pickPrescription(launcher: ActivityResultLauncher<Intent>) {
        ImagePicker.with(requireActivity())
            .crop(9f, 16f)
            /* Final image resolution will be less than 1080 x 1080 (Optional) */
            .maxResultSize(1080, 1080, true)
            /* make user able to pick picture from gallery or capture it */
            .provider(ImageProvider.BOTH)
            .createIntentFromDialog { launcher.launch(it) }
    }
}