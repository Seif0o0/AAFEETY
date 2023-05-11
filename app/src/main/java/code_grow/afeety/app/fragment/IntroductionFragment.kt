package code_grow.afeety.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import code_grow.afeety.app.R
import code_grow.afeety.app.databinding.FragmentIntroductionBinding
import code_grow.afeety.app.utils.Localize

class IntroductionFragment : Fragment() {
    private lateinit var binding: FragmentIntroductionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentIntroductionBinding.inflate(inflater, container, false)

        when (arguments?.getInt("indexParam")) {
            0 -> {
                binding.introImage.setImageResource(R.mipmap.intro_1)
                binding.introText.text = getString(R.string.intro_1)
            }
            1 -> {
                binding.introImage.setImageResource(R.mipmap.intro_1)
                binding.introText.text = getString(R.string.intro_2)
            }
            else -> {
                binding.introImage.setImageResource(R.mipmap.intro_1)
                binding.introText.text = getString(R.string.intro_1)
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(index: Int) = IntroductionFragment().apply {
            arguments = Bundle().apply {
                putInt("indexParam", index)
            }
        }
    }
}