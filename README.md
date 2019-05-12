# Earl - Evaluate Application Risk Level

![logo](https://github.com/apetenchea/Earl/blob/master/app/src/main/ic_launcher-web.png)

Uses a neural network for malware detection. The network was trained using approximately 1 million samples, split 50/50 into clean and malware.  
More details about how the network was trained can be found [here](https://gist.github.com/apetenchea/c729f9a8a4606f8b4a8ecfce92a4b3a6) (though not the actual net I used).
  
The accuracy obtained on the test set is 95%.
  
Currently, the following features are extracted using the Android API:
* APK size
* version code
* number of receivers
* number of services
* number of activities
* number of content providers
* 122 permissions
