package code_grow.afeety.app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.adapter.IntroductionSliderAdapter
import code_grow.afeety.app.databinding.FragmentIntroductionContainerBinding
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.utils.Localize

class IntroductionContainerFragment : Fragment() {
    private lateinit var switchBtn: ViewPager2.OnPageChangeCallback
    private lateinit var binding: FragmentIntroductionContainerBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentIntroductionContainerBinding.inflate(inflater, container, false)
        val adapter = IntroductionSliderAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPagerIndicator.setViewPager2(binding.viewPager)

        UserInfo.isFirstTime = false

        switchBtn = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    binding.skipText.visibility = View.INVISIBLE
                    binding.nextButton.text = getString(R.string.start)
                } else {
                    binding.nextButton.text = getString(R.string.next)
                    binding.skipText.visibility = View.VISIBLE
                }
            }
        }
        binding.viewPager.registerOnPageChangeCallback(switchBtn)

        binding.nextButton.setOnClickListener {
            if (binding.viewPager.currentItem < 2) {
                binding.viewPager.currentItem++
            } else {
                navigateTo()
            }
        }

        binding.skipText.setOnClickListener {
            navigateTo()
        }

        return binding.root
    }

    private fun navigateTo() {
        if (UserInfo.isSigned) {
            requireActivity().finish()
            startActivity(Intent(requireContext(), MainActivity::class.java))
        } else {
            requireActivity().finish()
            startActivity(Intent(requireContext(), LoginRegisterActivity::class.java).apply {
                putExtra("hideGuestLogin", false)
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(switchBtn)
    }
}