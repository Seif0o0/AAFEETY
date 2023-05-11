package code_grow.afeety.app.adapter.binding_adapter


import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import code_grow.afeety.app.R
import code_grow.afeety.app.model.DoctorSchedule
import code_grow.afeety.app.retrofit.Resource
import com.google.android.material.button.MaterialButton
import com.makeramen.roundedimageview.RoundedImageView
import java.io.File


@BindingAdapter("error_message")
fun EditText.setLoginError(errorMessage: String) {
    if (errorMessage.isNotEmpty())
        error = errorMessage
}

@BindingAdapter("terms_conditions_status")
fun ImageView.setTermsStatus(status: String) {
    if (status == "0")
        setImageResource(R.drawable.ic_register_empty_check_box)
    else
        setImageResource(R.drawable.ic_register_checked_check_box)
}

@BindingAdapter("terms_conditions_error")
fun TextView.setTermsVisibility(boolean: Boolean) {
    visibility = if (boolean)
        View.VISIBLE
    else
        View.GONE
}

@BindingAdapter("otp_error")
fun TextView.setOtpError(index: Int) {
    when (index) {
        0 -> visibility = View.GONE
        1 -> {
            visibility = View.VISIBLE
            text = context.getString(R.string.empty_field_error_message)
        }
        2 -> {
            visibility = View.VISIBLE
            text = context.getString(R.string.otp_length_error_message)
        }
    }
}

@BindingAdapter("drop_down_list")
fun AutoCompleteTextView.setDropDownList(list: Array<String>) {
    setAdapter(ArrayAdapter(context, R.layout.register_dropdown_item, list))
}

@BindingAdapter("rounded_image_url")
fun RoundedImageView.loadImage(url: String) {
    if (url == "logo")
        setImageResource(R.mipmap.logo)
    else
        Glide.with(this)
            .load(url)
            .into(this)
}

@BindingAdapter("speciality_image_url")
fun RoundedImageView.loadSpecialityImage(url: String) {
    if (url == R.drawable.ic_all_specialities.toString())
        setImageResource(url.toInt())
    else
        Glide.with(this)
            .load(url)
            .into(this)
}

@BindingAdapter("clicked_speciality_color")
fun TextView.setSpecialityColor(isClicked: Boolean) {
    setTextColor(
        ContextCompat.getColor(
            context,
            if (isClicked) R.color.hospitals_main_color else R.color.hospitals_main_text_color
        )
    )
}

@BindingAdapter("image_url")
fun ImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .into(this)
}

@BindingAdapter("prescription")
fun ImageView.loadPrescription(url: String) {
    val file = File(Uri.parse(url)!!.path!!)
    Glide.with(this)
        .load(file)
        .into(this)
}


@BindingAdapter("progress_state")
fun FrameLayout.setProgressVisibility(state: Resource) {
    visibility = when (state) {
        is Resource.Loading -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("first_progress_state", "second_progress_state")
fun FrameLayout.setProgressVisibility(firstState: Resource, secondState: Resource) {
    visibility = if (firstState is Resource.Loading || secondState is Resource.Loading) View.VISIBLE
    else View.GONE
}

@BindingAdapter("empty_list_state")
fun TextView.setEmptyListTextVisibility(state: Resource) {
    visibility = when (state) {
        is Resource.Empty -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("empty_list_state")
fun ViewPager2.setEmptyListViewPagerVisibility(state: Resource) {
    visibility = when (state) {
        is Resource.Empty -> View.GONE
        else -> View.VISIBLE
    }
}

@BindingAdapter("empty_list_state")
fun RecyclerView.setListVisibility(state: Resource) {
    visibility = when (state) {
        is Resource.Success<*> -> View.VISIBLE
        else -> View.INVISIBLE
    }
}

@BindingAdapter("failed_state")
fun ConstraintLayout.setErrorLayoutVisibility(state: Resource) {
    visibility = when (state) {
        is Resource.Failed -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("rate")
fun TextView.setRate(rate: Float) {
//    val roundedRate =
//        String.format("%.1f", rate).toFloat()
//    if (roundedRate % 1 == 0f)
//        this.text = roundedRate.toInt().toString()
//    else
    this.text = rate.toString()
}

@BindingAdapter("price")
fun TextView.setPrice(price: Double) {
//    val roundedRate =
//        String.format("%.1f", rate).toFloat()
//    if (roundedRate % 1 == 0f)
//        this.text = roundedRate.toInt().toString()
//    else
    this.text = price.toString()
}


@BindingAdapter("formatted_price")
fun TextView.setFormattedPrice(price: Double) {
//    val roundedRate =
//        String.format("%.1f", rate).toFloat()
//    if (roundedRate % 1 == 0f)
//        this.text = roundedRate.toInt().toString()
//    else
    this.text = String.format(context.getString(R.string.price_with_currency), price.toString())
}

@BindingAdapter("formatted_booking_count", "is_lab")
fun TextView.setFormattedBookingCount(count: Int, isLab: Boolean) {
    text = String.format(
        context.getString(if (isLab) R.string.lab_examinations_count else R.string.hospital_reservations_count),
        count
    )
}

@BindingAdapter("is_home_visit")
fun Group.setGroupVisibility(isHomeVisit: Int) {
    visibility = if (isHomeVisit == 1) View.VISIBLE else View.GONE
}

@BindingAdapter("is_home_visit")
fun MaterialButton.setMaterialBtnVisibility(isHomeVisit: Int) {
    visibility = if (isHomeVisit == 1) View.VISIBLE else View.GONE
}

@BindingAdapter("play_animation")
fun LottieAnimationView.startAnimation(errorMessage: String?) {
    if (errorMessage == null)
        return
    setAnimation(
        when (errorMessage) {
            context.getString(R.string.no_internet_connection) -> "no_internet_connection.json"
            else -> "error_dialog_animation.json"
        }
    )
    playAnimation()
    repeatCount = LottieDrawable.INFINITE
}

@BindingAdapter("cancel_animation")
fun LottieAnimationView.cancelAnimation(boolean: Boolean) {
    if (boolean)
        cancelAnimation()
}

@BindingAdapter("schedule_day")
fun TextView.setScheduleDay(dayNumber: Int) {
    var index = dayNumber
    if (index == 0)
        index = 7
    else
        index--
    text = context.getStringArray(R.array.days_of_week)[index]
}

@BindingAdapter("schedule_day")
fun EditText.setScheduleDay(dayNumber: Int) {
    setText(context.getStringArray(R.array.days_of_week)[dayNumber])
}

@BindingAdapter("is_clicked")
fun LinearLayout.isDoctorScheduleClicked(isClicked: Boolean) {
    background = if (isClicked)
        ContextCompat.getDrawable(context, R.drawable.doctor_selected_schedule_background)
    else
        ContextCompat.getDrawable(context, R.drawable.doctor_unselected_schedule_background)
}

@BindingAdapter("is_clicked")
fun ConstraintLayout.isDoctorSpecialityClicked(isClicked: Boolean) {
    background =
        ContextCompat.getDrawable(
            context,
            if (isClicked) R.drawable.clicked_doctor_speciality_background else R.drawable.idle_doctor_speciality_background
        )
}


@BindingAdapter("is_speciality_clicked")
fun RoundedImageView.isClicked(isClicked: Boolean) {
    Log.d("BindingAdapter", "isSpeciality: $isClicked")
    setBackgroundColor(
        ContextCompat.getColor(
            context,
            if (isClicked) R.color.hospitals_second_mini_background_color else android.R.color.transparent
        )
    )
}

@BindingAdapter("is_clicked")
fun TextView.scheduleClicked(isClicked: Boolean) {
    if (isClicked)
        setTextColor(ContextCompat.getColor(context, R.color.hospitals_background_color))
    else
        setTextColor(ContextCompat.getColor(context, R.color.hospitals_details_text_color))

}

@BindingAdapter("is_clicked")
fun ImageView.examinationClicked(isClicked: Boolean) {
    setImageResource(if (isClicked) R.drawable.ic_register_checked_check_box else R.drawable.ic_register_empty_check_box)
}

@BindingAdapter("booking_question")
fun ImageView.accessMedicalFile(value: Int) {
    setImageResource(if (value == 1) R.drawable.ic_register_checked_check_box else R.drawable.ic_register_empty_check_box)
}

@BindingAdapter("doctor_schedule")
fun TextView.setSelectedTime(schedule: DoctorSchedule?) {
    if (schedule == null) return
    text = schedule.startTime.plus(" - ").plus(schedule.endTime)
}

@BindingAdapter("review")
fun TextView.setReviewComment(review: String) {
    if (review.isEmpty()) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        text = review
    }
}

@BindingAdapter("index")
fun ImageView.setReviewTimeIcon(index: Int) {
    setImageResource(
        when (index) {
            0 -> R.drawable.ic_lab_review_time
            else -> R.drawable.ic_hospital_review_time/* 1 */
        }
    )
}

@BindingAdapter("report_state")
fun Group.setReportVisibility(state: Resource) {
    visibility = when (state) {
        is Resource.Success<*>, Resource.Loading -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("set_home_visit")
fun ImageView.setHomeVisit(status: String) {
    if (status == "0")
        setImageResource(R.drawable.ic_labs_empty_check_box)
    else
        setImageResource(R.drawable.ic_labs_checked_check_box)
}

@BindingAdapter("booking_status")
fun TextView.setBookingStatus(status: Int) {
    if (status == 0) {/* current */
        background = ContextCompat.getDrawable(context, R.drawable.current_reservation_background)
        text = context.getString(R.string.current_reservation)
        setTextColor(ContextCompat.getColor(context, R.color.labs_main_color))
    } else {/* (1) past */
        background = ContextCompat.getDrawable(context, R.drawable.past_reservation_background)
        text = context.getString(R.string.past_reservation)
        setTextColor(ContextCompat.getColor(context, R.color.my_reservations_done_status_color))
    }
}

@BindingAdapter("is_current")
fun TextView.setReviewTextVisibility(status: Int) {
    visibility = if (status == 0)
        View.GONE
    else
        View.VISIBLE
}

@BindingAdapter("is_current")
fun FrameLayout.setCancelBookingVisibility(status: Int) {
    visibility = if (status == 0)
        View.VISIBLE
    else
        View.GONE
}

@BindingAdapter("is_current_booking")
fun TextView.setStatusText(status: Int) {
    text = if (status == 0)
        context.getString(R.string.current_reservation)
    else
        context.getString(R.string.past_reservation)
}

@BindingAdapter("booking_number")
fun TextView.setBookingNumber(bookingNumber: Int) {
    text = "#".plus(bookingNumber)
}

@BindingAdapter("formatted_booking_date")
fun TextView.formattedDate(date: String) {
    if (!date.contains("-"))
        text = date
    else {
        val splitter = date.split("-")
        text =
            context.resources.getStringArray(R.array.short_months)[splitter[1].toInt() - 1].plus(" ${splitter[2]}, ${splitter[0]}")
    }
}

@BindingAdapter("examination_comment")
fun TextView.setExaminationComment(comment: String?) {
    if (comment.isNullOrEmpty())
        visibility = View.GONE
    else {
        visibility = View.VISIBLE
        text = comment
    }
}

@BindingAdapter("examination_comment")
fun View.setExaminationComment(comment: String?) {
    visibility = if (comment.isNullOrEmpty())
        View.INVISIBLE
    else
        View.VISIBLE
}

@BindingAdapter("order_number")
fun TextView.setOrderNumber(orderNumber: Int) {
    text = String.format(context.getString(R.string.order_number_prefix), orderNumber.toString())
}

@BindingAdapter("auto_complete_listener")
fun AutoCompleteTextView.removeError(emptyData: Boolean) {
    onItemClickListener =
        AdapterView.OnItemClickListener { _, _, _, _ ->
            error = null
        }
}

@BindingAdapter("cart_product_famous_name")
fun TextView.setFamousName(famousName: String) {
    text = famousName.ifEmpty { context.getString(R.string.cart_products) }
}