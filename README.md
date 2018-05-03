# Bedun Family 

## Bedun Family is an Android application providing a digital solution to the displacement of refugees across the globe.

![alt text](https://github.com/sefmiller/BedunFamily/blob/master/art/Screenshot_2018-04-30-22-04-08.png)![alt text](https://github.com/sefmiller/BedunFamily/blob/master/art/Screenshot_2018-04-30-22-04-20.png)![alt text](https://github.com/sefmiller/BedunFamily/blob/master/art/Screenshot_2018-04-30-22-06-01.png)![alt text](https://github.com/sefmiller/BedunFamily/blob/master/art/Screenshot_2018-04-30-22-06-10.png)![alt text](https://github.com/sefmiller/BedunFamily/blob/master/art/Screenshot_2018-04-30-22-07-08.png)![alt text](https://github.com/sefmiller/BedunFamily/blob/master/art/Screenshot_2018-04-30-22-08-18.png)![alt text](https://github.com/sefmiller/BedunFamily/blob/master/art/Screenshot_2018-04-30-22-07-49.png)![alt text](https://github.com/sefmiller/BedunFamily/blob/master/art/Screenshot_2018-04-30-22-08-11.png)


The app can be used by a refugee to trace a family member missing along the refugee route, or by a certified aid worker, acting on their behalf.

A refugee's details are recorded and are then searchable by other users. Enough details are recorded to be identified by a family member, without disclosing personal, sensitive information. A search algorithm sources the most probable matches. Upon accepting a possible match, each user exchanges a short voice message. If both users confirm that the voice message belongs to their missing family member, then an aid organisation in the local area is brought in to establish re-unifacion. 

The concept arose from personal experieince of working within a refugee camp with other aid workers. A mobile phone is the most valuable posession of a refugee, as a means of staying in contact with home and family and also sending and recieving currency. Whilst crossing borders along the route, smart phones can be lost, stolen or contacts lost through changing to a sim in a different locale. The app provides a solution by empowering refugees to re-connect with their family members. The app is available in 14 Languages

#### The project is currently unfinished, awaiting an injection of funding.


###  Dependencies 

The Following modules are used in the app:

WheelPicker https://github.com/AigeStudio/WheelPicker. Wheel picker used to select the language. 

CircleImageView https://github.com/hdodenhof/CircleImageView. Used to provide a circular, bordered imageview to contain image files.

IntlPhoneInput https://github.com/AlmogBaku/IntlPhoneInput. Contains all country codes/ flags to simplify a user inputting their phone number.

Amazon AWS S3. Used to securely store, upload and download data such as voicemails and image files.

Amazon AWS Cognito. Used to authenticate and identify user and allocate access tokens.

Amazon AWS Dynamo. Provides a database instance.

Amazon EC2. Hosts a linux web server. The SQL database tables are accessed through phpMyadmin. Php scripts are used to fetch the data from the database and our encoded into JSON, which is then readable by the application. 

Amazon SNS. Texts messaging facilitates authenticating and identifying a user and retrieve losts details.

Glide https://github.com/bumptech/glide.  Used to retrieve images from URLS and cache into local memory. Used primarily for its superior automatic image recycling.

ThreeTenABP https://github.com/JakeWharton/ThreeTenABP. Backport of the updated Android Time & Date libraries (JSR 310) to support older Android OS.

### Building

The project builds with Jcenter. 

## Authors

* **Sefton Howie** - 
 

## License

 Copyright 2018 Sefton Howie

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
