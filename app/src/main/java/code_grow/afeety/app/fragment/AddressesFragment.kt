package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.afeety.app.R
import code_grow.afeety.app.adapter.AddressesAdapter
import code_grow.afeety.app.adapter.OnAddressItemClickListener
import code_grow.afeety.app.databinding.FragmentAddressesBinding
import code_grow.afeety.app.model.Address
import code_grow.afeety.app.repository.CartRepository
import code_grow.afeety.app.retrofit.Resource
import code_grow.afeety.app.utils.CustomDialog
import code_grow.afeety.app.utils.Localize
import code_grow.afeety.app.view_model.AddressesViewModel
import code_grow.afeety.app.view_model.AddressesViewModelFactory

private const val TAG = "AddressesFragment"

class AddressesFragment : Fragment() {
    private lateinit var binding: FragmentAddressesBinding
    private lateinit var viewModel: AddressesViewModel

    private lateinit var selectedAddress: Address
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        val viewModelFactory = AddressesViewModelFactory(
            requireActivity().application,
            CartRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[AddressesViewModel::class.java]

        if (this::binding.isInitialized) {
            binding
        } else {
            binding = FragmentAddressesBinding.inflate(inflater, container, false)
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()

            binding.navigateBack.setOnClickListener {
                findNavController().popBackStack()
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        findNavController().popBackStack()
                    }

                })

            binding.addNewAddress.setOnClickListener {
                findNavController().navigate(
                    AddressesFragmentDirections.actionAddressesFragmentToAddAddressFragment(
                        AddressesFragmentArgs.fromBundle(requireArguments()).medicines
                    )
                )
            }

            binding.nextButton.setOnClickListener {
                if (!::selectedAddress.isInitialized) {
                    CustomDialog.showErrorDialog(
                        context = requireContext(),
                        errorMessage = getString(R.string.delivery_address_required)
                    )
                    return@setOnClickListener
                }

                findNavController().navigate(
                    AddressesFragmentDirections.actionAddressesFragmentToOrderDetailsFragment(
                        medicines = AddressesFragmentArgs.fromBundle(requireArguments()).medicines,
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


}