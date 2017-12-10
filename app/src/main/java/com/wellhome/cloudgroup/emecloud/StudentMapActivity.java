package com.wellhome.cloudgroup.emecloud;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StudentMapActivity extends Activity {
	//private String sno = null;//�õ�ʱ��ǵ�ת��int
	
	private MapView mMapView; 
	private BaiduMap mBaiduMap;
	private Context context;
	private TextView mTextView;
	//��λ���
	private LocationClient mLocationClient;
	private MyLocationListener myLocationListener;
	private boolean isFirstIn = true;
	private double mLatitude;
	private double mLongitude;
	//�Զ��嶨λͼ��
	private BitmapDescriptor mIconLocation;
	private MyOrientionListener mOrientionListener;
	private float mCurrentX;
	private LocationMode mLocationMode;
	//��ת���б�
	private Button btnToList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext()); 
        setContentView(R.layout.student_map); 
        this.context=this;
        initView();
      //��ʼ����λ
      	initLocation();
	}
	private void initLocation() {
		mLocationMode = LocationMode.NORMAL;
		mLocationClient = new LocationClient(this);
		myLocationListener = new MyLocationListener();
		//�Լ���������ע��
		mLocationClient.registerLocationListener(myLocationListener);
		//��LocationClient����һЩ����
		LocationClientOption option = new LocationClientOption();
		//��������
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);//��õ�ַ��������
		option.setOpenGps(true);//��GPS
		//�����������һ������
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		//��ʼ����λͼ��
		mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
		                                                                                                                                            
		mOrientionListener = new MyOrientionListener(context);
		mOrientionListener.setmOnOrientationListener(new MyOrientionListener.OnOrientationListener() {
			
			@Override
			public void onOrientationChanged(float x) {
				mCurrentX=x;
			}
		});
	}
	private void initView() {
		//Intent intent = getIntent();
		//sno = intent.getStringExtra("sno");
		mMapView=(MapView) findViewById(R.id.bmapView_s);
		mBaiduMap = mMapView.getMap();
		//���øս�ȥ�ĵ�ͼ�Ŵ����
		MapStatusUpdate msu = 
				MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		mTextView = (TextView) findViewById(R.id.id_textView_s);
		btnToList = (Button) findViewById(R.id.btn_list_s);
		//��ת���б�
		btnToList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, Classmate_List.class);
				startActivity(intent);
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.id_map_common:
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
				break;
			case R.id.id_map_site:
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
				break;
			case R.id.id_map_traffic:
				//���ʵʱ��ͨ�Ƿ�� Ĭ��Ϊoff
				if(mBaiduMap.isTrafficEnabled()){
					mBaiduMap.setTrafficEnabled(false);
					item.setTitle("ʵʱ��ͨ(off)");
				}else{
					mBaiduMap.setTrafficEnabled(true);
					item.setTitle("ʵʱ��ͨ(on)");
				}
				break;
			case R.id.id_map_location:
				centerToMyLocation();
				break;
			//ģʽ�л�
			case R.id.id_map_mode_common:
				mLocationMode=LocationMode.NORMAL;
				break;
			case R.id.id_map_mode_following:
				mLocationMode=LocationMode.FOLLOWING;
				break;
			case R.id.id_map_mode_compass:
				mLocationMode=LocationMode.COMPASS;
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onResume() {
		super.onResume();
		//��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
	}
	@Override
	protected void onStart() {
		super.onStart();
		//������λ
		mBaiduMap.setMyLocationEnabled(true);
		if(!mLocationClient.isStarted()){//�ж϶�λ�Ƿ�����
			mLocationClient.start();
			//�������򴫸���
			mOrientionListener.start();
		}
	}
	@Override
	protected void onStop() {
		super.onStop();
		//ֹͣ��λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		//ֹͣ���򴫸���
		mOrientionListener.stop();
	}
	@Override
	protected void onPause() {
		super.onPause();
		//��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		 //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
	}//��λ���ҵ�λ��
	private void centerToMyLocation() {
		LatLng latLng = new LatLng(mLatitude,mLongitude);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);//��ͼ��λ��ʹ�ö�����Ч��
	}
	
	private class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			MyLocationData data = new MyLocationData.Builder()//buildģʽ ��һ��Builder�ڲ���
				.direction(mCurrentX)//����
				.accuracy(location.getRadius())//����
				.latitude(location.getLatitude())//
				.longitude(location.getLongitude())//
				.build();
			mBaiduMap.setMyLocationData(data);
			//�����Զ���ͼ��
			MyLocationConfiguration config = new MyLocationConfiguration(
					mLocationMode,true,mIconLocation);
			mBaiduMap.setMyLocationConfigeration(config);
			//���¾�γ��
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();
			if (isFirstIn)
			{
				centerToMyLocation();
				isFirstIn = false;
				// �����Ի�����ʾ��λ��Ϣ��
				Builder builder = new Builder(context);
				builder.setTitle("Ϊ����õĶ�λ��Ϣ��");
				builder.setMessage("��ǰλ�ã�" + location.getAddrStr() + "\n"
						+ "���б�ţ�" + location.getCityCode() + "\n" + "��λʱ�䣺"
						+ location.getTime() + "\n" + "��ǰγ�ȣ�"
						+ location.getLatitude() + "\n" + "��ǰ���ȣ�"
						+ location.getLongitude());
				builder.setPositiveButton("ȷ��", null);
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
				mTextView.setText(location.getAddrStr());
			}
		}
		
	}
	
}
