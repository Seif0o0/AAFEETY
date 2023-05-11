package code_grow.afeety.app.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import code_grow.afeety.app.R
import code_grow.afeety.app.databinding.FragmentHospitalMoreDetailsBinding
import code_grow.afeety.app.utils.Localize
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HospitalMoreDetailsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentHospitalMoreDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Localize.changeLanguage("ar", requireContext())
        binding = FragmentHospitalMoreDetailsBinding.inflate(inflater, container, false)

        binding.moreDetailsInfo.text = requireArguments().getString("moreInfo")

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fixMapScrolling()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun fixMapScrolling() {
        binding.fakeImage.setOnTouchListener { _, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    binding.nestedScrollView.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    binding.nestedScrollView.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    binding.nestedScrollView.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> {
                    true
                }
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        if (requireArguments().containsKey("latitude")) {

            val hospitalLocation = LatLng(
                requireArguments().getDouble("latitude"),
                requireArguments().getDouble("longitude")
            )
            googleMap.addMarker(
                MarkerOptions()
                    .position(hospitalLocation)
                    .title(getString(R.string.hospital_location))
                    .icon(bitmapFromVector())
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hospitalLocation, 12f))
        }

    }

    private fun bitmapFromVector(): BitmapDescriptor {
        val vectorDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_hospital_location_on_map)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    companion object {
        @JvmStatic
        fun newInstance(latitude: Double, longitude: Double, moreInfo: String) =
            HospitalMoreDetailsFragment().apply {
                arguments = Bundle().apply {
                    putDouble("latitude", latitude)
                    putDouble("longitude", longitude)
                    putString("moreInfo", moreInfo)
                }
            }
    }
}
