package org.apache.cordova.PluginDevice;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
 



// Example is the 3rd parameter in javascript part
public class PluginDevice extends CordovaPlugin  { 

    @Override                   // action is the 4th parameter, args is the array from 5th parameter in javascript part
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("requestLocationUpdates")) {

            MainActivity cls = new MainActivity();
            cls.requestLocationUpdates();

            return true;
        }

        if (action.equals("removeLocationUpdates")) {

            MainActivity cls = new MainActivity();
            cls.removeLocationUpdates();

            return true;
        }

        return false; // Returning false results in a "MethodNotFound" error.
    }
    
}



