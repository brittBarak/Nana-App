package com.playground.britt.nana_app


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_blank.*
import java.io.IOException
import java.io.InputStream
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
val filePaths = arrayOf<String>("card1.jpg");
private var selectedImage: Bitmap? = null


/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BlankFragment : Fragment(), AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, id: Long) {
        selectedImage = getBitmapFromAsset(this!!.context!!, filePaths[position])
        if (selectedImage != null) {
            imageView.setImageBitmap(selectedImage)
        }

    }

    fun onClassified(modelTitle: String, topLabels: List<String>, executionTime: Long) {
        val builder = SpannableStringBuilder()
        val boldSpan = StyleSpan(Typeface.BOLD)
        builder.append("$modelTitle - time: $executionTime\n\n", boldSpan, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        if (topLabels === Collections.EMPTY_LIST || topLabels.size == 0) {
            builder.append("No results..")
        } else {
            for (s in topLabels) {
                builder.append(s)
                builder.append("\n")
            }

            tv_result.setText(builder)
        }
    }


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setSpinner()

        btn_label_local.setOnClickListener(View.OnClickListener {

//            var res = RecognizeText().execute(selectedImage!!, context )

//            Toast.makeText(context, res, Toast.LENGTH_LONG).show()
        })
    }

    private fun setSpinner() {

        val adapter = ArrayAdapter<String>(activity, android.R.layout
                .simple_spinner_dropdown_item, getImageNames())
        spinner.setAdapter(adapter)
        spinner.onItemSelectedListener = this
    }

    private fun getImageNames(): List<String> {
        val items = ArrayList<String>()
        for (i in filePaths.indices) {
            items.add("Image " + (i + 1))
        }
        return items
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String? = null, param2: String? = null) =
                BlankFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    fun getBitmapFromAsset(context: Context, filePath: String): Bitmap? {
        val assetManager = context.assets

        val `is`: InputStream
        var bitmap: Bitmap? = null
        try {
            `is` = assetManager.open(filePath)
            bitmap = BitmapFactory.decodeStream(`is`)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }
}
