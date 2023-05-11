package code_grow.afeety.app.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.QuestionsTabsPagerAdapter
import code_grow.afeety.app.databinding.FragmentAskDoctorBinding
import code_grow.afeety.app.utils.Localize
import com.google.android.material.tabs.TabLayoutMediator

class AskDoctorFragment : Fragment() {
    private lateinit var binding: FragmentAskDoctorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())

        binding = FragmentAskDoctorBinding.inflate(inflater, container, false)

        /*
         *  if english (doctor_image) scaleX = -1
         *  else scaleX = 1
         */

        // hide bottom navigation while creating this fragment
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(false)

        val adapter = QuestionsTabsPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        val tabsNames = resources.getStringArray(R.array.questions_tabs)

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = tabsNames[position]
        }.attach()

        binding.askNow.setOnClickListener {
            findNavController().navigate(
                AskDoctorFragmentDirections.actionAskDoctorFragmentToSendQuestionFragment()
            )
        }

        binding.searchEditText.tag = "false";
        binding.searchEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus -> binding.searchEditText.tag = "$hasFocus" }



        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.searchEditText.tag.toString().lowercase() == "true") {
                    adapter.filter(s.toString(),binding.viewPager.currentItem)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        return binding.root
    }
}