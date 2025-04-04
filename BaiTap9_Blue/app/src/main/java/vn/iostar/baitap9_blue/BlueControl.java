package vn.iostar.baitap9_blue;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BlueControl extends AppCompatActivity {
    ImageButton btnTb1, btnTb2, btnDis;
    TextView txt1, txtMAC;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    Set<BluetoothDevice> pairedDevices; // Đổi tên để tránh nhầm lẫn với pairedDevices1
    String address = null;
    private ProgressDialog progress;
    int flagLamp1 = 0; // Khởi tạo giá trị mặc định
    int flagLamp2 = 0; // Khởi tạo giá trị mặc định

    // SPP UUID
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS); // Nhận địa chỉ từ MainActivity
        setContentView(R.layout.activity_control);

        // Ánh xạ giao diện
        btnTb1 = findViewById(R.id.btnTb1);
        btnTb2 = findViewById(R.id.btnTb2);
        txt1 = findViewById(R.id.textV1);
        txtMAC = findViewById(R.id.textViewMAC);
        btnDis = findViewById(R.id.btnDisc);

        // Kết nối Bluetooth
        new ConnectBT().execute();

        // Sự kiện nhấn nút
        btnTb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thietTb1();
            }
        });

        btnTb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thietTb2();
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });
    }

    // Hàm điều khiển thiết bị 1
    private void thietTb1() {
        if (btSocket != null) {
            try {
                if (flagLamp1 == 0) {
                    flagLamp1 = 1;
                    btnTb1.setBackgroundResource(R.drawable.tb1on);
                    btSocket.getOutputStream().write("A".getBytes());
                    txt1.setText("Thiết bị số 1 đang bật");
                } else {
                    flagLamp1 = 0;
                    btnTb1.setBackgroundResource(R.drawable.tb1off);
                    btSocket.getOutputStream().write("a".getBytes());
                    txt1.setText("Thiết bị số 1 đang tắt");
                }
            } catch (IOException e) {
                msg("Lỗi khi gửi lệnh!");
            }
        } else {
            msg("Chưa kết nối Bluetooth!");
        }
    }

    // Hàm điều khiển thiết bị 2
    private void thietTb2() {
        if (btSocket != null) {
            try {
                if (flagLamp2 == 0) {
                    flagLamp2 = 1;
                    btnTb2.setBackgroundResource(R.drawable.tb2on);
                    btSocket.getOutputStream().write("7".getBytes());
                    txt1.setText("Thiết bị số 7 đang bật");
                } else {
                    flagLamp2 = 0;
                    btnTb2.setBackgroundResource(R.drawable.tb2off);
                    btSocket.getOutputStream().write("6".getBytes());
                    txt1.setText("Thiết bị số 7 đang tắt");
                }
            } catch (IOException e) {
                msg("Lỗi khi gửi lệnh!");
            }
        } else {
            msg("Chưa kết nối Bluetooth!");
        }
    }

    // Hàm ngắt kết nối
    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
                isBtConnected = false;
                msg("Đã ngắt kết nối!");
            } catch (IOException e) {
                msg("Lỗi khi ngắt kết nối!");
            }
            finish(); // Quay lại màn hình trước
        }
    }

    // Lớp AsyncTask để kết nối Bluetooth
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(BlueControl.this, "Đang kết nối...", "Xin vui lòng đợi!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);

                    if (ActivityCompat.checkSelfPermission(BlueControl.this,
                            android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }

                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                msg("Kết nối thất bại! Kiểm tra thiết bị.");
                finish();
            } else {
                msg("Kết nối thành công.");
                isBtConnected = true;
                pairedDevicesList();
            }
            progress.dismiss();
        }
    }

    // Hiển thị thông tin thiết bị đã kết nối
    private void pairedDevicesList() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        pairedDevices = myBluetooth.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                if (bt.getAddress().equals(address)) {
                    txtMAC.setText(bt.getName() + " - " + bt.getAddress());
                    break;
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Không tìm thấy thiết bị kết nối.", Toast.LENGTH_LONG).show();
        }
    }

    // Hàm hiển thị thông báo
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}