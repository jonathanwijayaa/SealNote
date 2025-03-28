package com.example.sealnote.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sealnote.R

class AuthenticationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.authentication, container, false)

        val tabFingerprint = view.findViewById<TextView>(R.id.tab_fingerprint)
        val tabFace = view.findViewById<TextView>(R.id.tab_face)
        val iconAuth = view.findViewById<ImageView>(R.id.icon_auth)
        val textAuth = view.findViewById<TextView>(R.id.text_auth)

        // Default ke Face ID
        setFaceMode(tabFingerprint, tabFace, iconAuth, textAuth)

        tabFingerprint.setOnClickListener {
            setFingerprintMode(tabFingerprint, tabFace, iconAuth, textAuth)
        }

        tabFace.setOnClickListener {
            setFaceMode(tabFingerprint, tabFace, iconAuth, textAuth)
        }

        return view
    }

    private fun setFingerprintMode(
        tabFingerprint: TextView, tabFace: TextView,
        iconAuth: ImageView, textAuth: TextView
    ) {
        tabFingerprint.setBackgroundResource(R.drawable.tab_selected)
        tabFace.setBackgroundResource(R.drawable.tab_unselected)
        iconAuth.setImageResource(R.drawable.ic_finger)
        textAuth.text = "Cover the entire fingerprint sensor."
    }

    private fun setFaceMode(
        tabFingerprint: TextView, tabFace: TextView,
        iconAuth: ImageView, textAuth: TextView
    ) {
        tabFace.setBackgroundResource(R.drawable.tab_selected)
        tabFingerprint.setBackgroundResource(R.drawable.tab_unselected)
        iconAuth.setImageResource(R.drawable.ic_face_id)
        textAuth.text = "We need to detect your face."
    }
}
