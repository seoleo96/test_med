package com.example.testmed.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.testmed.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.net.URI

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>,
) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!
    private var currentAnimator: Animator? = null
    private var shortAnimationDuration: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
    }

    override fun onStart() {
        super.onStart()
        Log.d("STARTON", "onStart: ")
        updateStatePatient(online)
        updateToken()
    }

    private fun updateToken() {
        val ref = DB.reference.child("patients")
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null){
                    val uid = user.uid
                    val token = task.result
                    ref.child(uid).child("token").setValue(token)
                }
            }
    }


    private val refState = DB.reference.child("patients")

    fun updateStatePatient(online: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            val uid = user.uid
            lifecycleScope.launch(Dispatchers.IO) {
                refState.child(uid).child("state").setValue(online)
            }
        }
    }


    fun zoomImageFromThumb(
        thumbView: View,
        imageUri: Uri,
        imageView: ImageView,
        container: ConstraintLayout,
        containerSen: LinearLayout,
        containerToolbar: ConstraintLayout,
        recyclerChat: RecyclerView,
    ) {
        invisibleContents(
            container,
            containerSen,
            containerToolbar,
            recyclerChat,
        )
        currentAnimator?.cancel()
        val expandedImageView: ImageView = imageView
        expandedImageView.setImageURI(imageUri)
        val startBoundsInt = Rect()
        val finalBoundsInt = Rect()
        val globalOffset = Point()
        thumbView.getGlobalVisibleRect(startBoundsInt)
        container.getGlobalVisibleRect(finalBoundsInt, globalOffset)
        startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
        finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)
        val startBounds = RectF(startBoundsInt)
        val finalBounds = RectF(finalBoundsInt)
        val startScale: Float
        if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
            startScale = startBounds.height() / finalBounds.height()
            val startWidth: Float = startScale * finalBounds.width()
            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            startScale = startBounds.width() / finalBounds.width()
            val startHeight: Float = startScale * finalBounds.height()
            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }
        expandedImageView.isVisible = true
        expandedImageView.pivotX = 0f
        expandedImageView.pivotY = 0f
        currentAnimator = AnimatorSet().apply {
            play(
                ObjectAnimator.ofFloat(
                    expandedImageView,
                    View.X,
                    startBounds.left,
                    finalBounds.left)
            ).apply {
                with(ObjectAnimator.ofFloat(expandedImageView,
                    View.Y,
                    startBounds.top,
                    finalBounds.top))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f))
            }
            duration = shortAnimationDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    currentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    currentAnimator = null
                }
            })
            start()
        }
        expandedImageView.setOnClickListener {
            currentAnimator?.cancel()
            currentAnimator = AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left)).apply {
                    with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                    with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale))
                    with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale))
                }
                duration = shortAnimationDuration.toLong()
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        thumbView.alpha = 1f
                        expandedImageView.isGone = true
                        containerSen.isVisible = true
                        containerToolbar.isVisible = true
                        recyclerChat.isVisible = true
                        container.setBackgroundResource(R.color.background_root)
                        currentAnimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        containerSen.isVisible = true
                        containerToolbar.isVisible = true
                        recyclerChat.isVisible = true
                        thumbView.alpha = 1f
                        expandedImageView.isGone = true
                        container.setBackgroundResource(R.color.background_root)
                        currentAnimator = null
                    }
                })
                start()
            }
        }
    }

    fun setImage(url:String, imageView: ImageView, type : String = ""){
        if(type == ""){
            Glide.with(imageView.context).load(url).into(imageView)
        }else{
            Glide.with(imageView.context).load(url).fitCenter().into(imageView)
        }
    }

    fun getCachedPhotoURI(urlPhoto: String): Uri {
        val imageUri: Uri?
        val file: File = Glide.with(binding.root.context).asFile()
            .load(urlPhoto).submit().get()
        val path: String = file.path
        imageUri = "file://$path".toUri()
        return imageUri
    }

    private fun invisibleContents(
        container: ConstraintLayout,
        containerSen: LinearLayout,
        containerToolbar: ConstraintLayout,
        recyclerChat: RecyclerView,
    ) {
        containerSen.isVisible = false
        containerToolbar.isVisible = false
        recyclerChat.isVisible = false
        container.setBackgroundResource(R.color.defaultColor)
    }


    override fun onStop() {
        super.onStop()
        Log.d("STOPON", "onStop: ")
        requireView().hideKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}