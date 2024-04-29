package com.example.photoapplication;

import android.graphics.*;
import java.io.*;

public class Serializer implements Serializable {
	private Bitmap bitmap; // Bitmap object to be serialized

	// Constructor to initialize the Serializer with a Bitmap object
	public Serializer(Bitmap bitmap) { this.bitmap = bitmap; }

	// Getter method to retrieve the Bitmap object
	public Bitmap getBitmap() { return bitmap; }

	// Custom serialization method for writing object to ObjectOutputStream
	private void writeObject(ObjectOutputStream oos) throws IOException {
		// Convert Bitmap to byte array
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();

		// Write the length of byte array followed by the byte array itself
		oos.writeInt(byteArray.length);
		oos.write(byteArray);
	}

	// Custom deserialization method for reading object from ObjectInputStream
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// Read the length of byte array
		int bufferLength = ois.readInt();
		byte[] byteArray = new byte[bufferLength];
		int pos = 0;
		// Read the byte array in chunks until all bytes are read
		do {
			int read = ois.read(byteArray, pos, bufferLength - pos);
			if (read != -1) {
				pos += read;
			} else {
				break;
			}
		} while (pos < bufferLength);

		// Decode the byte array back to a Bitmap
		bitmap = BitmapFactory.decodeByteArray(byteArray, 0, bufferLength);
	}
}