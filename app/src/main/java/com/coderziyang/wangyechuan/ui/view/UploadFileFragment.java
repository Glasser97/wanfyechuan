package com.coderziyang.wangyechuan.ui.view;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.coderziyang.wangyechuan.AppContext;
import com.coderziyang.wangyechuan.Constant;
import com.coderziyang.wangyechuan.R;
import com.coderziyang.wangyechuan.core_func.utils.FileUtils;
import com.coderziyang.wangyechuan.core_func.utils.ToastUtils;
import com.coderziyang.wangyechuan.entity.FileInfo;
import com.coderziyang.wangyechuan.ui.adapter.FileInfoAdapter;
import com.coderziyang.wangyechuan.ui.adapter.UploadFileInfoAdapter;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link UploadFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FILE_TYPE= "type";

    /**
     * the UI
     */
    GridView gv;
    ProgressBar pb;

    private int mType = FileInfo.TYPE_APK;
    private List<FileInfo> mFileInfoList;
    private UploadFileInfoAdapter mFileInfoAdapter;


    public UploadFileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type
     * @return A new instance of fragment UploadFileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFileFragment newInstance(int type) {
        UploadFileFragment fragment = new UploadFileFragment();
        Bundle args = new Bundle();
        args.putInt(FILE_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = (getArguments()!= null)? getArguments().getInt(FILE_TYPE):FileInfo.TYPE_APK;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_upload_file, container, false);
        gv = rootView.findViewById(R.id.gv);
        pb = rootView.findViewById(R.id.pb);
        switch(mType){
            case FileInfo.TYPE_APK:{
                gv.setNumColumns(4);
                break;
            }
            case FileInfo.TYPE_JPG:{
                gv.setNumColumns(3);
                break;
            }
            case FileInfo.TYPE_MP3:{
                gv.setNumColumns(1);
                break;
            }
            case FileInfo.TYPE_MP4:{
                gv.setNumColumns(1);
                break;
            }
        }
        init();
        return rootView;
    }

    private void init(){
        if(mType == FileInfo.TYPE_APK){
            new GetUploadFileInfoListTask(getContext(),FileInfo.TYPE_APK).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }else if(mType == FileInfo.TYPE_JPG){
            new GetUploadFileInfoListTask(getContext(),FileInfo.TYPE_JPG).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }else if(mType == FileInfo.TYPE_MP3){
            new GetUploadFileInfoListTask(getContext(),FileInfo.TYPE_MP3).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }else if(mType == FileInfo.TYPE_MP4){
            new GetUploadFileInfoListTask(getContext(),FileInfo.TYPE_MP4) .executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }
        gv.setOnItemClickListener((parent,view,position,id)->{
            FileInfo fileInfo = mFileInfoList.get(position);
            FileUtils.openFile(getContext(),fileInfo.getFilePath());
        });
        gv.setOnCreateContextMenuListener((contextMenu,view,contextMenuInfo)->{
            contextMenu.add(Menu.NONE,0,0,R.string.str_delete);
        });
    }


    /**
     * 文件删除
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {
            case 0:
                //mFileInfoList.remove(menuInfo.position);
                mFileInfoAdapter.remove(menuInfo.position);
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 显示以及隐藏进度条
     */
    public void showProgressBar(){
        if (pb != null){
            pb.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar(){
        if (pb != null){
            pb.setVisibility(View.GONE);
        }
    }


    class GetUploadFileInfoListTask extends AsyncTask<String, Integer, List<FileInfo>> {

        Context sContext = null;
        int sType = FileInfo.TYPE_APK;
        List<FileInfo> sFileInfoList = null;
        File dir = FileUtils.DEFAULT_UPLOAD_PATH;

        GetUploadFileInfoListTask(Context context,int type){
            this.sContext = context;
            this.sType = type;
        }

        @Override
        protected void onPreExecute(){
            showProgressBar();
            super.onPreExecute();
        }

        @Override
        protected List<FileInfo> doInBackground(String... strings) {
            sFileInfoList = FileUtils.getFileInfoList(dir,sType);
            sFileInfoList = FileUtils.getDetailFileInfos(sContext,sFileInfoList,sType);
            mFileInfoList = sFileInfoList;
            return sFileInfoList;
        }

        @Override
        protected void onPostExecute(List<FileInfo> list){
            hideProgressBar();
            if(sFileInfoList != null && sFileInfoList.size() > 0){
                if(mType == FileInfo.TYPE_APK){
                    mFileInfoAdapter = new UploadFileInfoAdapter(sContext,sFileInfoList, FileInfo.TYPE_APK);
                    gv.setAdapter(mFileInfoAdapter);
                }else if(mType == FileInfo.TYPE_JPG){
                    mFileInfoAdapter = new UploadFileInfoAdapter(sContext,sFileInfoList, FileInfo.TYPE_JPG);
                    gv.setAdapter(mFileInfoAdapter);
                }else if(mType == FileInfo.TYPE_MP3){
                    mFileInfoAdapter = new UploadFileInfoAdapter(sContext,sFileInfoList, FileInfo.TYPE_MP3);
                    gv.setAdapter(mFileInfoAdapter);
                }else if(mType == FileInfo.TYPE_MP4){
                    mFileInfoAdapter = new UploadFileInfoAdapter(sContext,sFileInfoList, FileInfo.TYPE_MP4);
                    gv.setAdapter(mFileInfoAdapter);
                }
            }else{
                ToastUtils.show(sContext,sContext.getResources().getString(R.string.tip_has_no_apk_info));
            }
        }
    }

}
