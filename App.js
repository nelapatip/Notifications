/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

 import React from 'react';
 import {
   Alert,
   AppState,
   Text,
   View,
 } from 'react-native';
 
 import PushNotificationIOS from "@react-native-community/push-notification-ios";
 import firebase from 'react-native-firebase' 
 
 class App extends React.Component {
 
 
   constructor(props) {
     super(props)
     this.state = {
       token1: '',
       token2: '' 
     }
   }
   componentDidMount() {
      if (Platform.OS == 'android') {
        this.checkPermission();
        this.createNotificationListeners()
      }
      else {
        this.handlePushNotificationIOS()
      }
   }
 
  
   async checkPermission() {
    const enabled = await firebase.messaging().hasPermission();
    if (enabled) {
      this.getFCMToken();
    } else {
      this.requestPermission();
    }
  }

  async getFCMToken() {
    fcmToken = await firebase.messaging().getToken();
    
    console.log("FCM Token : " + fcmToken)
    this.setState({token1: fcmToken})

  }

  async requestPermission() {
    try {
      await firebase.messaging().requestPermission();
      // User has authorised
      this.getFCMToken();
    } catch (error) {
      // User has rejected permissions
      console.log('permission rejected');
    }
  }

  async createNotificationListeners() {
    /*
    * Triggered when a particular notification has been received in foreground
    * */
    this.notificationListener = firebase.notifications().onNotification((notification) => {
      const { title, body } = notification;
      this.showAlert(title, body);
    });

    /*
    * If your app is in background, you can listen for when a notification is clicked / tapped / opened as follows:
    * */

    this.notificationOpenedListener = firebase.notifications().onNotificationOpened((notificationOpen) => {

      Analytics.logEvent(CONSTANTS.FIREBASE_ANALYTICS.NOTIFICATION_CLICKED, { "platform": Platform.OS ,"userId":global.userID})
      const { title, body } = notificationOpen.notification.data;
  
      this.setState({isFromNotification:true},()=>{
      const alertObj = notificationOpen.notification.data;
      const { messageId, isFavourite } = alertObj;
      if(isFavourite === "true"){
        this.setState({notificationRouteScreen: "Favourites" , uuid: messageId , isFavourite:true})
      }else{
        this.setState({notificationRouteScreen: "Favourites" , uuid: messageId, isFavourite: false})  
      }    
    
    })
    });

    /*
    * If your app is closed, you can check if it was opened by a notification being clicked / tapped / opened as follows:
    * */
    const notificationOpen = await firebase.notifications().getInitialNotification();
    if (notificationOpen) {
      Analytics.logEvent(CONSTANTS.FIREBASE_ANALYTICS.NOTIFICATION_CLICKED, { "platform": Platform.OS ,"userId":global.userID})
      const { title, body } = notificationOpen.notification.data;
  
      this.setState({isFromNotification:true},()=>{
      const alertObj = notificationOpen.notification.data;
      const { messageId, isFavourite } = alertObj;
      if(isFavourite === "true"){
        this.setState({notificationRouteScreen: "Favourites" , uuid: messageId , isFavourite:true})
      }else{
        this.setState({notificationRouteScreen: "Favourites" , uuid: messageId, isFavourite: false})  
      }    
      
    })

    }

    /*
    * Triggered for data only payload in foreground
    * */
    this.messageListener = firebase.messaging().onMessage((message) => {
      //process data message
      console.log(JSON.stringify(message));
    });
  }

  showAlert(title, body) {
    Alert.alert(
      title, body,
      [
        { text: 'OK', onPress: () => console.log('OK Pressed') },
      ],
      { cancelable: false },
    );
  }

   handlePushNotificationIOS() {
    PushNotificationIOS.addEventListener('register', token => {
      //Get iOS device token here
      console.log("APNS Device Token ::" + token)
      this.setState({token1: token})
    })

    PushNotificationIOS.addEventListener('registrationError', registrationError => {
      this.setState({token2: 'Registration Error'})
      console.log(registrationError, '--')
    })

    PushNotificationIOS.addEventListener('localNotification', localNotification => {
    })



    PushNotificationIOS.addEventListener('notification', function (notification) {
      if (!notification) {
        return
      }

      if (AppState.currentState === 'background') {
       
      }
      else{
        const alert = notification.getAlert()
        // const data = notification.getData()
        const { title, body } = alert;
        // this.showAlert(title, body);
  
        Alert.alert(
          title, body,
          [
            { text: 'OK', onPress: () => console.log('OK Pressed') },
          ],
          { cancelable: false },
        );
  
      }
    })




    //When app is in killed state
    PushNotificationIOS.getInitialNotification().then(notification => {
      if (!notification) {
        return
      }

      if (notification != null) {
      }
    })
    
    PushNotificationIOS.requestPermissions()

    PushNotificationIOS.setApplicationIconBadgeNumber(0)
  }

 
    
 
 render() {
   return (
     <View style = {{flex:1, justifyContent: 'center', alignItems: 'center'}}>
       <Text>This app is to text push notifications</Text>
       <Text style = {{fontSize: 15, fontWeight: '500', marginTop: 20, marginLeft: 8, marginRight: 8}}>Push notificationIOS Success: {this.state.token1}</Text>
       <Text style = {{fontSize: 15, fontWeight: '500', marginTop: 30, marginLeft: 8, marginRight: 8}}>Push notificationIOS Error:   {this.state.token2}</Text>
       </View>
   );
 };
 
 }
 
 export default App;