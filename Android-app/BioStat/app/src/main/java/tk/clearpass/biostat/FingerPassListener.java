/**
 * Created by Nick on 1/31/16. He really wishes that Samsung would have written some documentation
 *  for this module - it would have made life MUCH easier.
 */

package tk.clearpass.biostat;

import com.samsung.android.sdk.pass.SpassFingerprint;


public class FingerPassListener implements SpassFingerprint.IdentifyListener {

        public void onFinished(int eventStatus) {
            // It is called when fingerprint identification is finished.
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                // Identify operation succeeded with fingerprint
            } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                // Identify operation succeeded with alternative password
            } else {
                // Identify operation failed with given eventStatus. // STATUS_TIMEOUT_FAILED
                // STATUS_USER_CANCELLED
                // STATUS_AUTHENTIFICATION_FAILED
                // STATUS_QUALITY_FAILED
            }
        }

        public void onReady() {
            // It is called when fingerprint identification is ready after
            // startIdentify() is called.
        }

        public void onStarted() {
            // It is called when the user touches the fingerprint sensor after
            // startIdentify() is called.
        }

}