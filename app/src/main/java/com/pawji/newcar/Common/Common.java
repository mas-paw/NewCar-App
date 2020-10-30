package com.pawji.newcar.Common;

import com.pawji.newcar.ui.mobil.MobilModel;
import com.pawji.newcar.ui.status.StatusTempModel;

public class Common {
    public static MobilModel selectedCar;
    public static StatusTempModel selectedStatus;

    public static final String tambahKaryawan = "http://192.168.100.54/newcar/karyawan/tambahKaryawan.php";
    public static final String updateKaryawan = "http://192.168.100.54/newcar/karyawan/updateKaryawan.php";
    public static final String deleteKaryawan = "http://192.168.100.54/newcar/karyawan/deleteKaryawan.php";
    public static final String getKaryawan = "http://192.168.100.54/newcar/karyawan/getKaryawan.php";

    public static final String tambahSupir = "http://192.168.100.54/newcar/supir/tambahSupir.php";
    public static final String updateSupir = "http://192.168.100.54/newcar/supir/updateSupir.php";
    public static final String deleteSupir = "http://192.168.100.54/newcar/supir/deleteSupir.php";
    public static final String getSupir = "http://192.168.100.54/newcar/supir/getSupir.php";

    public static final String mobilUpload = "http://192.168.100.54/newcar/mobil/upload.php";
    public static final String getMobil = "http://192.168.100.54/newcar/mobil/getMobil.php";
    public static final String getSingleMobil = "http://newcar-orc.000webhostapp.com/mobil/getSingleMobil.php?{kode_mobil}";
    public static final String deleteMobil= "http://192.168.100.54/newcar/mobil/deleteMobil.php";
    public static final String updateMobil = "http://192.168.100.54/newcar/mobil/updateMobil.php";
    public static final String getRandomMobil = "http://192.168.100.54/newcar/mobil/getRandomMobil.php";

    public static final String tambahStatus = "http://192.168.100.54/newcar/status/tambahStatus.php";
    public static final String getStatus = "http://192.168.100.54/newcar/status/getStatus.php";
    public static final String getSingleStatus = "http://192.168.100.54/newcar/status/getSingleStatus.php";
    public static final String deleteStatus= "http://192.168.100.54/newcar/status/deleteStatus.php";

    public static final String tambahTransaksi = "http://192.168.100.54/newcar/transaksi/tambahTransaksi.php";
    public static final String getTransaksi= "http://192.168.100.54/newcar/transaksi/getTransaksi.php";

    public static final String getStatusUser = "http://192.168.100.54/newcar/status/getStatusUser.php";
    public static final String deleteStatusUser= "http://192.168.100.54/newcar/status/deleteStatusUser.php";
    public static final String deleteStatusSingleUser= "http://192.168.100.54/newcar/status/deleteStatusSingleUser.php";
    public static final String tambahStatusUser= "http://192.168.100.54/newcar/status/tambahStatusUser.php";
    public static final String updateStatusUser = "http://192.168.100.54/newcar/status/updateStatusUser.php";
}
