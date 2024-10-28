package com.example.mqtttesterforandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import dsem.mqtt4j.client.BrokerConnector;

public class MainActivity extends AppCompatActivity {
    LinearLayout linLayoutRole, linLayoutPub, linLayoutSub, linLayoutLog;
    FrameLayout frmLayoutDet;

    EditText etIP, etPort, etPubTopic, etPubMessage, etSubTopic, etLog;
    Button btnConnect, btnPublish, btnSubscribe;
    RadioGroup roleGroup;
    RadioButton radioPub, radioSub;
    String broker_ip;
    int broker_port;
    boolean isConnected, isPublisher;

    NetworkThread nt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        linLayoutRole = (LinearLayout) findViewById(R.id.linLayoutRole);
        linLayoutPub = (LinearLayout) findViewById(R.id.linLayoutPub);
        linLayoutSub = (LinearLayout) findViewById(R.id.linLayoutSub);
        linLayoutLog = (LinearLayout) findViewById(R.id.linLayoutLog);
        frmLayoutDet = (FrameLayout) findViewById(R.id.frmLayoutDet);

        etIP = (EditText) findViewById(R.id.etIP);
        etPort = (EditText) findViewById(R.id.etPort);
        etPubTopic = (EditText) findViewById(R.id.etPubTopic);
        etPubMessage = (EditText) findViewById(R.id.etPubMessage);
        etSubTopic = (EditText) findViewById(R.id.etSubTopic);

        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnPublish = (Button) findViewById(R.id.btnPublish);
        btnSubscribe = (Button) findViewById(R.id.btnSubscribe);

        roleGroup = (RadioGroup) findViewById(R.id.roleGroup);
        radioPub = (RadioButton) findViewById(R.id.radioPub);
        radioSub = (RadioButton) findViewById(R.id.radioSub);

        isConnected = false;
        isPublisher = false;

        nt = new NetworkThread(this);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected) {
                    broker_ip = etIP.getText().toString();
                    if (broker_ip == null || "".equals(broker_ip)){
                        Toast.makeText(getApplicationContext(),"Input MQTT Broker IP. Default is localhost.",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String port = etPort.getText().toString();
                    if ((port == null) || "".equals(port)){
                        Toast.makeText(getApplicationContext(),"Input MQTT Broker Port. Default is 14320.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    broker_port = Integer.parseInt(port);

                    nt.connectBroker();
                } else {
                    nt.disconnectBroker();
                }
            }
        });

        roleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (roleGroup.getCheckedRadioButtonId()==R.id.radioPub) {
                    linLayoutPub.setVisibility(View.VISIBLE);
                    linLayoutSub.setVisibility(View.INVISIBLE);
                    nt.registerPublisher();
                } else if (roleGroup.getCheckedRadioButtonId()==R.id.radioSub) {
                    linLayoutPub.setVisibility(View.INVISIBLE);
                    linLayoutSub.setVisibility(View.VISIBLE);
                }
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = etPubTopic.getText().toString();
                String message = etPubMessage.getText().toString();
                nt.publishMessage(topic, message);
            }
        });

        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = etSubTopic.getText().toString();
                nt.subscribe(topic);
            }
        });

    }

    public void addSubMsg(String message) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currtime = sdf.format(date);

        TextView tv = new TextView(getApplicationContext());
        tv.setText("[" + currtime + "] " + message);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(param);
        linLayoutLog.addView(tv,0);
    }

        class NetworkThread {
            Activity activity;
            BrokerConnector bc;

            public NetworkThread(Activity activity) {
                this.activity = activity;
            }

        public void connectBroker() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bc = new BrokerConnector(broker_ip, broker_port);

                        if (bc.connectBroker()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Connection successed.", Toast.LENGTH_SHORT).show();
                                    btnConnect.setText("disconnect");
                                    linLayoutRole.setVisibility(View.VISIBLE);
                                    frmLayoutDet.setVisibility(View.VISIBLE);
                                    isConnected = true;
                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Connection failed.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e("mqtt4j", e.getMessage());
                    }
                }
            }).start();
        }

        public void disconnectBroker() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (bc.disconnectBroker()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Disconnection successed.", Toast.LENGTH_SHORT).show();
                                    btnConnect.setText("connect");
                                    linLayoutRole.setVisibility(View.INVISIBLE);
                                    frmLayoutDet.setVisibility(View.INVISIBLE);
                                    isConnected = false;
                                }
                            });
                        } else {
                            Log.d("mqtt4j","disconnection broker failed");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Disconnection failed.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e("mqtt4j", e.getMessage());
                    }
                }
            }).start();
        }

        public void registerPublisher() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(!isPublisher) {
                            if (bc.registerPublisher()) {
                                isPublisher = true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "Publisher is registered.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        Log.e("mqtt4j", e.getMessage());
                    }
                }
            }).start();
        }

        public void publishMessage(String topic, String message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(isPublisher) {
                            if (bc.publishMessage(topic, message)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "Publish successed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "Publish failed" + message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        Log.e("mqtt4j", e.getMessage());
                    }
                }
            }).start();
        }

        public void subscribe(String topic) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bc.joinSubscriber(topic);

                        while (true) {
                            String message = bc.subscirbe();
                            if (message != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addSubMsg(message);
                                    }
                                });
                            } else {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("mqtt4j", e.getMessage());
                    }
                }
            }).start();
        }
    }

}