**HEIFReader** & **HEIFWriter** for Android Guide Line.

- Convert from HEIC to JPG/JPEG/PNG.
- Convert JPG/JPEG/PNG to HEIC. 

------

#### Prerequisite

- Android SDK: API 28 and above.
- Working well as Android 28+ (for HEIFWriter).

#### Features

**HEIFWriter**

This project is a demo of a HEIFWriter from android developer as [link](https://developer.android.com/reference/androidx/heifwriter/HeifWriter). Please prefer any issue and welcome to pull request that we can discuss.

Code:

```kotlin
        // Using HEIFWriter from Google
        // https://developer.android.com/reference/androidx/heifwriter/HeifWriter
        // Warning: Support from Android 9.0+ & Does not support by Emulator.

        // Step 1: Loading JPG.PNG.JPEG files from asset and convert it to YUV or Bitmap
        val bitmap = BitmapFactory.decodeStream(assets.open("photo.jpeg"))
        val imageHeight = bitmap.height
        val imageWidth = bitmap.width
        val destination = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/photo.heic"
        Log.d("convertFile", destination)
        // Step 2: Create HEIF Writer instance & convert
        try {
            HeifWriter.Builder(destination, imageWidth, imageHeight, HeifWriter.INPUT_MODE_BITMAP)
                    .setQuality(90)             // Set Quality range [0,100]
                    .build().run {
                        start()
                        addBitmap(bitmap)       // addBitmap if the writer is using INPUT_MODE_BITMAP
                        stop(0)     // 0: mean infinitely running
                        close()                 // Close after use.
                    }
        } catch (ex: Exception) {
            // Throw exception in case the device is not supported like Android not above of 9.0 or cause by using Emulator
            ex.printStackTrace()
        }
```

**HEIFReader**

- Plan to use: [Nokia HEIF reader & writer](https://github.com/nokiatech/heif) & [mobileFFMPEG](https://github.com/tanersener/mobile-ffmpeg) to convert between HEIC and JPG/JPEG/PNG formats.
- How to do:
  - Build Nokia HEIF library and get the `aar` file.
  - Import to project.
  - Using **Nokia HEIF Library** to parse the data of HEIF format files.

```
				val heif = HEIF()
        // Step 1: Load file
        heif.load("HEIC.heic")
        // Step 2: Check type of HEIF format, cause it have many types: Still Image, Grid Image ... (Apple is using GridImage type for that format)
        when (heif.primaryImage) {
        	is GridImageItem -> {}
        	is IdentityImageItem -> {}
        	is OverlayImageItem -> {}
        	is HEVCImageItem -> {}
        	is AVCImageItem -> {}
        }
        
        // Step 3: In-case this is GridImageItem
        // Get size width, height
        val originalWidth = primaryImage.size.width
        val originalHeight = primaryImage.size.height
        // Getting original rotation degree of Original Image file
        val rotationDegree =
            (heif.itemProperties.findLast { it is RotateProperty } as? RotateProperty)?.rotation?.value
                ?: 0
        // Apple is using 48 tiles and join to 1 images, parse of its and then convert its to HEVC bitstream by FFMPEG
        for (rowIndex in 0 until primaryImage.rowCount) {
        	for (columnIndex in 0 until primaryImage.columnCount) {
        		 // Getting the tile image based column / row
             val hevcImageItem = primaryImage.getImage(columnIndex, rowIndex) as HEVCImageItem
             // Getting decoder config and then using mobile-ffmpeg to write to HEVC bitstream in local storage.
             ....
        	}
        	
        	// After getting 48 tiles as bitstream files, join its and merge into 1 files by ffmpeg.
        	....
        	// Loading Color profiles and attach it.
        	....        	
        	// Convert BitStream to JPG.JPEG.PNG file by FFMPEG
        	....
        	// Loading Exifdata and attach it.
					....
          // Delete temp files and finish convert.
        }
```

All of the code is still not publishing yet.

**Demo**

I'm successfully do all the converting between HEIF format for Android and publish that code into Google Play as here. Try to test it and welcome any questions.

![https://play.google.com/store/apps/details?id=com.cellhubs.multipleconverterpro](google-play-badge.png)

#### License

```
MIT License

Copyright (c) [2019] kingvhit

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```