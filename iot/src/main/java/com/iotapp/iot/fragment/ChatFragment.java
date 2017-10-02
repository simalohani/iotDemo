package com.iotapp.iot.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.iotapp.iot.R;
import com.iotapp.iot.adapter.ListAdapter;
import com.iotapp.iot.custom.FragmentInteractionListener;
import com.iotapp.iot.modal.ChatVO;
import com.iotapp.iot.modal.view.FragmentHandler;
import com.iotapp.iot.utility.Constant;
import com.iotapp.iot.utility.Util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class ChatFragment extends Fragment {

    public static final int CHAT_DATA = 1;
    public static final int SIGNUP = 2;
    public static final int SIGNUPACK = 3;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RESULT_SETTINGS = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int GALERY_PIC = 3;

    Handler chatHandler;
    ArrayList<ChatVO> chatList = new ArrayList();
    ListAdapter adapter;
    ListView listChatView;
    SharedPreferences sharedPrefs;
    String user;
    ImageView selImage;
    Context context;
    private FragmentInteractionListener mListener;

    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("chatFragment", "onCreate");
        context = getActivity().getApplicationContext();
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("chatFragment", "onCreateView");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.chat_layout, container, false);
        this.listChatView = ((ListView) rootView.findViewById(R.id.listconvertion));
        this.adapter = new ListAdapter(context, new ArrayList<ChatVO>());
        this.listChatView.setAdapter(this.adapter);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        initChatHandler();
        Util.getInstance().setHandler(chatHandler);
        addPublishButtonListener(rootView);

        this.selImage = ((ImageView) rootView.findViewById(R.id.plus));
        this.setHasOptionsMenu(true);
        selImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                getActivity().openOptionsMenu();
            }
        });
        return rootView;

    }
    private void setCurrFragment(){
        FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.setCurrFragment(this);
        fragmentHandler.setScrNo(Constant.CHAT_FRAGMENT);
        fragmentHandler.setData(null);
        fragmentHandler.setIsBackFragmentMove(false);
        fragmentHandler.setIsNextFragmentMove(false);
        fragmentHandler.setTitle("Chatting");
        mListener.onFragmentInteraction(fragmentHandler);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        this.context = activity;
        Log.e("chatFragment", "onAttach");
        try {
            mListener = (FragmentInteractionListener) activity;
            setCurrFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("chatFragment","onDetach");
        mListener = null;
        chatHandler =null;
        adapter =null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(getContext(), UserSettingActivity.class);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                Cursor c = getContext().getContentResolver().query(selectedImage, filePath,
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

    private void initChatHandler() {
        chatHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                 String data;
                switch (msg.what) {
                    case CHAT_DATA:
                        data = msg.getData().getString("chat");
                        Log.e("CHAT_DATA", data);
                        if (data != null) {
                            displayChat(data);
                        }
                        break;
                    case SIGNUP:

                        data = msg.getData().getString("signup");
                        if (data != null) {
                            HashMap object = new HashMap();
                            object.put(Constant.ACTION, Constant.ACTION_PUBLISH_ACK);
                            object.put("data", data);
                            mListener.sendData(data);
                        }
                        System.out.println("received=" + data);
                        break;
                    case SIGNUPACK:

                        data = msg.getData().getString("signupack");
                        if (data != null) {
                            Toast.makeText(getContext(), data, Toast.LENGTH_LONG).show();
                        }
                        System.out.println("received=" + data);
                        break;
                }
            }
        };

    }

    private void addPublishButtonListener(final View rootView) {
        user = sharedPrefs.getString("prefUsername", "NULL");
        Button publishButton = (Button) rootView.findViewById(R.id.buttonPublish);
        final EditText m = (EditText) rootView.findViewById(R.id.editTextMessage);
        final TextView result = (TextView) rootView.findViewById(R.id.textResultStatus);
        publishButton.setOnClickListener(new View.OnClickListener() {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            @Override
            public void onClick(View arg0) {
                inputMethodManager.hideSoftInputFromWindow(
                        result.getWindowToken(), 0);
                String topic = "chatstart";// t.getText().toString().trim();
                String message = user + "/" + m.getText().toString().trim();
                m.getText().clear();
                if (topic != null && topic.isEmpty() == false
                        && message != null && message.isEmpty() == false) {
                    result.setText("");
                    HashMap data = new HashMap();
                    data.put(Constant.ACTION,Constant.ACTION_PUBLISH_MSG);
                    data.put("topic",topic);
                    data.put("message", message);
                    mListener.sendData(data);
                } else {
                    result.setText("Topic and message required.");
                }
            }
        });
    }

    private void displayChat(String data) {

        try{
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

            this.adapter = new ListAdapter(context, chatList);
            adapter.setChatList(chatList);
            this.listChatView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();


        }
        }catch(Exception e){
            e.printStackTrace();
             Log.e("displayChat ->",e.toString());
        }
    }

    private void addItemsToList(String msg, String name, String date,
                                String picturePath, String imMsgText, String isImage) {

        HashMap data = new HashMap();
        data.put(Constant.ACTION,Constant.ACTION_PUBLISH_IMG);
        data.put("user",user);
        data.put("picturePath", picturePath);
        mListener.sendData(data);

    }

    /*private void addSubscribeButtonListener() {

        TextView result = (TextView) findViewById(R.id.textResultStatus);
        // EditText t = (EditText) findViewById(R.id.EditTextTopic);
        String topic = "chatstart";// t.getText().toString().trim();
        // inputMethodManager.hideSoftInputFromWindow(result.getWindowToken(),
        // 0);

        if (topic != null && topic.isEmpty() == false) {
            result.setText("");
            Bundle data = new Bundle();
            data.putCharSequence(MQTTservice.TOPIC, topic);
            Message msg = Message.obtain(null, MQTTservice.SUBSCRIBE);
            msg.setData(data);
            msg.replyTo = serviceHandler;
            try {
                service.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
                result.setText("Subscribe failed with exception:"
                        + e.getMessage());
            }
        } else {
            result.setText("Topic required.");
        }

    }*/
    private void showUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        StringBuilder builder = new StringBuilder();

        builder.append("\n Username: "
                + sharedPrefs.getString("prefUsername", "NULL"));

    }
}
