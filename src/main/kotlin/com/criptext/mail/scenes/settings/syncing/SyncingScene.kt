package com.criptext.mail.scenes.settings.syncing

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.beardedhen.androidbootstrap.BootstrapProgressBar
import com.criptext.mail.R
import com.criptext.mail.utils.DeviceUtils
import com.criptext.mail.utils.UIMessage
import com.criptext.mail.utils.UIUtils
import com.criptext.mail.utils.generaldatasource.data.GeneralResult
import com.criptext.mail.utils.getLocalizedUIMessage
import com.criptext.mail.utils.ui.RetryManualSyncAlertDialogNewDevice


interface SyncingScene{

    fun attachView(model: SyncingModel, syncingUIObserver: SyncingUIObserver)
    fun showMessage(message : UIMessage)
    fun disableCancelSync()
    fun setProgress(message: UIMessage, progress: Int)
    fun startSucceedAnimation(launchMailboxScene: (
            linkingUIObserver: SyncingUIObserver) -> Unit)
    fun showRetrySyncDialog(result: GeneralResult)


    var syncingUIObserver: SyncingUIObserver?

    class Default(private val view: View): SyncingScene {

        private val context = view.context

        override var syncingUIObserver: SyncingUIObserver? = null



        private val loadingView: View = view.findViewById(R.id.viewAnimation)
        private val textViewStatus: TextView = view.findViewById(R.id.textViewStatus)
        private val textViewEmail: TextView = view.findViewById(R.id.textViewEmail)
        private val progressBar: BootstrapProgressBar = view.findViewById(R.id.progressBar)
        private val progressBarNumber: TextView = view.findViewById(R.id.percentage_advanced)
        private val cancelSyncText: TextView = view.findViewById(R.id.cancelSync)
        private val retrySyncDialog: RetryManualSyncAlertDialogNewDevice = RetryManualSyncAlertDialogNewDevice(context)
        private val oldDevice: ImageView = view.findViewById(R.id.imageViewDevice1)

        override fun attachView(model: SyncingModel, syncingUIObserver: SyncingUIObserver) {
            this.syncingUIObserver = syncingUIObserver
            textViewEmail.text = model.email
            when (model.deviceType){
                DeviceUtils.DeviceType.PC, DeviceUtils.DeviceType.MacStore, DeviceUtils.DeviceType.MacInstaller,
                DeviceUtils.DeviceType.WindowsInstaller, DeviceUtils.DeviceType.WindowsStore,
                DeviceUtils.DeviceType.LinuxInstaller -> oldDevice.setImageResource(R.drawable.device_pc)
                else -> oldDevice.setImageResource(R.drawable.device_m)
            }
            cancelSyncText.setOnClickListener {
                this.syncingUIObserver?.onCancelSync()
            }
        }

        override fun startSucceedAnimation(launchMailboxScene: (
                linkingUIObserver: SyncingUIObserver) -> Unit) {
            loadingView.post {
                val animSucceed = initSuccessAnimatorSet(view.findViewById(R.id.imageViewSucceed))

                animSucceed.addListener(object : Animation.AnimationListener, Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {
                    }

                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        launchMailboxScene(syncingUIObserver!!)
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                    override fun onAnimationRepeat(p0: Animation?) {
                    }

                    override fun onAnimationEnd(p0: Animation?) {
                    }

                    override fun onAnimationStart(p0: Animation?) {
                    }

                })

                animSucceed.start()
            }

            textViewEmail.setTextColor(ContextCompat.getColor(view.context, R.color.colorAccent))
            textViewStatus.text = view.resources.getText(R.string.device_ready)
        }


        private fun initSuccessAnimatorSet(viewSucceed: View): AnimatorSet {

            val animArray = arrayOfNulls<ObjectAnimator>(1)
            val animObj = ObjectAnimator.ofFloat(viewSucceed, "alpha", 0.0f, 1f)
            initSuccessObjectAnim(animObj, 0)
            animArray[0] = animObj

            val animSet = AnimatorSet()
            animSet.playTogether(*animArray)
            animSet.duration = 1000
            return animSet
        }

        private fun initSuccessObjectAnim(animObj: ObjectAnimator, delay: Long) {
            if (delay > 0)
                animObj.startDelay = delay
        }

        override fun setProgress(message: UIMessage, progress: Int) {
            textViewStatus.text = context.getLocalizedUIMessage(message)
            if (progress >= 96) {
                progressBar.progress = 100
                progressBarNumber.text = 100.toString().plus("%")
            }
            else {
                val anim = UIUtils.animationForProgressBar(progressBar, progress,
                        progressBarNumber, 1000)
                anim.start()
            }
        }

        override fun showRetrySyncDialog(result: GeneralResult) {
            retrySyncDialog.showLinkDeviceAuthDialog(syncingUIObserver, result)
        }

        override fun disableCancelSync() {
            cancelSyncText.isEnabled = false
            cancelSyncText.visibility = View.INVISIBLE
        }


        override fun showMessage(message: UIMessage) {
            val duration = Toast.LENGTH_LONG
            val toast = Toast.makeText(
                    context,
                    context.getLocalizedUIMessage(message),
                    duration)
            toast.show()
        }

    }
}