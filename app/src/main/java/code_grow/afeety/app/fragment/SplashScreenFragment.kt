package code_grow.afeety.app.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import code_grow.afeety.app.R
import code_grow.afeety.app.activity.LoginRegisterActivity
import code_grow.afeety.app.activity.MainActivity
import code_grow.afeety.app.databinding.FragmentSplashScreenBinding
import code_grow.afeety.app.kot_pref.UserInfo
import code_grow.afeety.app.utils.Localize

@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment() {
    private lateinit var binding: FragmentSplashScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentSplashScreenBinding.inflate(inflater,container,false)
//        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            if (UserInfo.isFirstTime)
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(
                        SplashScreenFragmentDirections.actionSplashScreenFragmentToIntroductionFragment()
                    )
            else {
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
        }, 2300)
    }
}