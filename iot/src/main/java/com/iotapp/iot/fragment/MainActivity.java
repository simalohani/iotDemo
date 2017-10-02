package com.iotapp.iot.fragment;

import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.iotapp.iot.R;
import com.iotapp.iot.adapter.ListAdapter;
import com.iotapp.iot.controller.ChatController;
import com.iotapp.iot.modal.ChatVO;
import com.iotapp.iot.security.Authentication;
import com.iotapp.iot.service.MQTTservice;
import com.iotapp.iot.utility.Constant;
import com.iotapp.iot.utility.Util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, UserSettingActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                return true;
            case R.id.takePhoto:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(
                        Environment.getExternalStorageDirectory(),
                        "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, 2);

                return true;
            case R.id.chooseFromGallery:
                intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                showUserSettings();
                break;
            case TAKE_PHOTO: {
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    String picturePath = f.getAbsolutePath();
                    // viewImage.setImageBitmap(bitmap);
                    // viewImage.setVisibility(View.VISIBLE);

                    String path = Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    int piclength = picturePath.length();
                    System.out.println("Picture Length====" + piclength);
                    addItemsToList("", "Me", "1 Min Ago", picturePath, "2", "local");
                    // f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System
                            .currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case GALERY_PIC: {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = this.getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                int piclength = picturePath.length();
                System.out.println("Picture Length====" + piclength);
                c.close();
                addItemsToList("", "Me", "1 Min Ago", picturePath, "2", "local");
            }
            break;
        }

    }







    private void displayChat(String data) {
        if (data.contains("/")) {
            ChatVO chat = new ChatVO();
            int userlimit = data.indexOf("/");
            if (data.contains("img")) {
                chat.setImage(data.substring(userlimit + 1, data.length()));
                chat.setName(data.substring(0, userlimit - 3));
                chat.setMsg("");
            } else {
                chat.setImage("");
                chat.setName(data.substring(0, userlimit));
                chat.setMsg(data.substring(userlimit + 1, data.length()));
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "hh:mm a");
            Date date = new Date();
            System.out.println("Date converted to String: "
                    + dateFormat.format(date));

            chat.setDate(dateFormat.format(date));

            chatList.add(chat);
            this.adapter.notifyDataSetChanged();
        }
    }



    private void addItemsToList(String msg, String name, String date,
                                String picturePath, String imMsgText, String isImage) {
        sendImage(picturePath);

    }

    public void sendImage(String Img) {
        String topic = "chatstart";
        String message = user + "img/" + new ChatController().getImg(Img);

        if (topic != null && topic.isEmpty() == false && Img != null
                && Img.isEmpty() == false) {

            Bundle data = new Bundle();
            data.putCharSequence(MQTTservice.TOPIC, topic);
            data.putCharSequence(MQTTservice.MESSAGE, message);
            Message msg = Message.obtain(null, MQTTservice.PUBLISH);
            msg.setData(data);
            msg.replyTo = serviceHandler;
            try {
                service.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
                // result.setText("Publish failed with exception:" +
                // e.getMessage());
            }
        } else {
            // result.setText("Topic and message required.");
        }

    }

    public void publisgMsg(String topic, String msg) {
        Bundle data = new Bundle();
        data.putCharSequence(MQTTservice.TOPIC, topic);
        data.putCharSequence(MQTTservice.MESSAGE, msg);
        Message mesg = Message.obtain(null, MQTTservice.PUBLISH);
        mesg.setData(data);
        mesg.replyTo = serviceHandler;
        try {
            Util.getInstance().getService().send(mesg);
        } catch (RemoteException e) {
            e.printStackTrace();

        }
    }

    private void publishAck(String msg) {
        Authentication authentication = new Authentication(this);
        publisgMsg(Constant.signUpAck, authentication.userAuthentication(msg.toString()));
    }

    public class PushReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent i) {
            String topic = i.getStringExtra(MQTTservice.TOPIC);
            String message = i.getStringExtra(MQTTservice.MESSAGE);
            Toast.makeText(context,
                    "Push message received - " + topic + ":" + message,
                    Toast.LENGTH_LONG).show();
        }
    }

}*/
