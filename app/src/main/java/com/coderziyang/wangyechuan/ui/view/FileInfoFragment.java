package com.coderziyang.wangyechuan.ui.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.coderziyang.wangyechuan.AppContext;
import com.coderziyang.wangyechuan.R;
import com.coderziyang.wangyechuan.core_func.utils.FileUtils;
import com.coderziyang.wangyechuan.core_func.utils.ToastUtils;
import com.coderziyang.wangyechuan.entity.FileInfo;
import com.coderziyang.wangyechuan.ui.ChooseFileActivity;
import com.coderziyang.wangyechuan.ui.adapter.FileInfoAdapter;

import java.util.List;

public class FileInfoFragment extends Fragment {
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
    private FileInfoAdapter mFileInfoAdapter;


    public FileInfoFragment() {
        super();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type
     * @return A new instance of fragment FileInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FileInfoFragment newInstance(int type) {
        FileInfoFragment fragment = new FileInfoFragment();
        Bundle args = new Bundle();
        args.putInt(FILE_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mType = getArguments() != null ? getArguments().getInt(FILE_TYPE) : FileInfo.TYPE_APK;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_file_info, container, false);
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
            new GetFileInfoListTask(getContext(),FileInfo.TYPE_APK).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }else if(mType == FileInfo.TYPE_JPG){
            new GetFileInfoListTask(getContext(),FileInfo.TYPE_JPG).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }else if(mType == FileInfo.TYPE_MP3){
            new GetFileInfoListTask(getContext(),FileInfo.TYPE_MP3).executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }else if(mType == FileInfo.TYPE_MP4){
            new GetFileInfoListTask(getContext(),FileInfo.TYPE_MP4) .executeOnExecutor(AppContext.MAIN_EXECUTOR);
        }

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfo fileInfo = mFileInfoList.get(position);
                if(AppContext.getAppContext().isExist(fileInfo)){
                    AppContext.getAppContext().deleteFileInfo(fileInfo);
                    updateSelectedView();
                }else{
                    AppContext.getAppContext().addFileInfo(fileInfo);
                    // 添加任务的动画
                    updateSelectedView();
                    // TODO
                }
                mFileInfoAdapter.notifyDataSetChanged();
            }
        });
    }



    @Override
    public void onResume(){
        updateFileInfoAdapter();
        super.onResume();
    }

    /**
     * 更新FileInfoAdapter
     */
    public void updateFileInfoAdapter(){
        if(mFileInfoAdapter!=null){
            mFileInfoAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

    @Override
    public void onDetach(){
        super.onDetach();
    }

    /**
     * 更新ChooseFileActivity选中View
     */
    private void updateSelectedView(){
        if(getActivity()!=null && (getActivity() instanceof ChooseFileActivity)){
            ChooseFileActivity chooseFileActivity = (ChooseFileActivity) getActivity();
            chooseFileActivity.getSelectedView();
        }
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


    class GetFileInfoListTask extends AsyncTask<String, Integer, List<FileInfo>>{
        Context sContext = null;
        int sType = FileInfo.TYPE_APK;
        List<FileInfo> sFileInfoList = null;

        GetFileInfoListTask(Context context,int type){
            this.sContext = context;
            this.sType = type;
        }

        @Override
        protected void onPreExecute(){
            showProgressBar();
            super.onPreExecute();
        }

        @Override
        protected List doInBackground(String... params){
            if(sType == FileInfo.TYPE_APK){
                sFileInfoList = FileUtils.getSpecificTypeFiles(sContext, new String[]{FileInfo.EXTEND_APK});
                sFileInfoList = FileUtils.getDetailFileInfos(sContext, sFileInfoList, FileInfo.TYPE_APK);
            }else if(sType == FileInfo.TYPE_JPG){
                sFileInfoList = FileUtils.getSpecificTypeFiles(sContext, new String[]{FileInfo.EXTEND_JPG, FileInfo.EXTEND_JPEG});
                sFileInfoList = FileUtils.getDetailFileInfos(sContext, sFileInfoList, FileInfo.TYPE_JPG);
            }else if(sType == FileInfo.TYPE_MP3){
                sFileInfoList = FileUtils.getSpecificTypeFiles(sContext, new String[]{FileInfo.EXTEND_MP3});
                sFileInfoList = FileUtils.getDetailFileInfos(sContext, sFileInfoList, FileInfo.TYPE_MP3);
            }else if(sType == FileInfo.TYPE_MP4){
                sFileInfoList = FileUtils.getSpecificTypeFiles(sContext, new String[]{FileInfo.EXTEND_MP4});
                sFileInfoList = FileUtils.getDetailFileInfos(sContext, sFileInfoList,FileInfo.TYPE_MP4);
            }
            mFileInfoList = sFileInfoList;
            return sFileInfoList;
        }

        @Override
        protected void onPostExecute(List<FileInfo> list){
            hideProgressBar();
            if(sFileInfoList != null && sFileInfoList.size() > 0){
                if(mType == FileInfo.TYPE_APK){
                    mFileInfoAdapter = new FileInfoAdapter(sContext,sFileInfoList, FileInfo.TYPE_APK);
                    gv.setAdapter(mFileInfoAdapter);
                }else if(mType == FileInfo.TYPE_JPG){
                    mFileInfoAdapter = new FileInfoAdapter(sContext,sFileInfoList, FileInfo.TYPE_JPG);
                    gv.setAdapter(mFileInfoAdapter);
                }else if(mType == FileInfo.TYPE_MP3){
                    mFileInfoAdapter = new FileInfoAdapter(sContext,sFileInfoList, FileInfo.TYPE_MP3);
                    gv.setAdapter(mFileInfoAdapter);
                }else if(mType == FileInfo.TYPE_MP4){
                    mFileInfoAdapter = new FileInfoAdapter(sContext,sFileInfoList, FileInfo.TYPE_MP4);
                    gv.setAdapter(mFileInfoAdapter);
                }
            }else{
                ToastUtils.show(sContext,sContext.getResources().getString(R.string.tip_has_no_apk_info));
            }
        }
    }




}
