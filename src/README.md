#TEST or PROD flag is set in CryptoUtil
SecurityUtil.getInstance().setFlag(PROD_FLAG);

#BASELINE DATA is loaded on init()
LoginServlet, SignUpServlet

#BUILD
Build Project -> Build Artifacts
Project build configuration set to automatically do this.
Or of course do manually with IDE or GRADLE

#DEPLOY to Jelastic CLOUD
Install Jelastic plugin


