package com.stupidtree.hita.fragments.popup;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stupidtree.hita.HITAApplication;
import com.stupidtree.hita.R;
import com.stupidtree.hita.timetable.TimetableCore;
import com.stupidtree.hita.util.ActivityUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.stupidtree.hita.HITAApplication.HContext;


public class FragmentEmptyClassroomDialog extends FragmentRadiusPopup {
    private String lhName;
    private String lhValue;
    private List<Map> detailRes;
    private RecyclerView detailPlaces;
    private detailListAdapter detailAdapter;
    private ProgressBar loading;
    private int pageCourseNumber;
    refreshDetailTask pageTask;
    private FragmentEmptyClassroomDialog(){

    }

    public static FragmentEmptyClassroomDialog getInstance(String lhName,String lhValue,int pageCourseNumber){
        Bundle b = new Bundle();
        b.putString("lhName",lhName);
        b.putString("lhValue",lhValue);
        b.putInt("pageCourseNumber",pageCourseNumber);
        FragmentEmptyClassroomDialog r = new FragmentEmptyClassroomDialog();
        r.setArguments(b);
        return r;
    }

    protected void stopTasks() {
        if(pageTask!=null) pageTask.cancel(true);
    }

    public void Refresh() {
        if(pageTask!=null) pageTask.cancel(true);
        pageTask = new refreshDetailTask(lhName,lhValue,detailRes,detailAdapter,loading,detailPlaces);
        pageTask.executeOnExecutor(HITAApplication.TPE);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        stopTasks();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lhName = getArguments().getString("lhName");
        lhValue = getArguments().getString("lhValue");
        pageCourseNumber = getArguments().getInt("pageCourseNumber");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = View.inflate(requireContext(), R.layout.dialog_emptyclassroom_detail, null);
        initList(v);
        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        Refresh();
    }

    void initList(View v){
        TextView title = v.findViewById(R.id.title);
        detailRes = new ArrayList<>();
        detailPlaces = v.findViewById(R.id.detail_list);
        loading =v.findViewById(R.id.detail_loading);
        detailAdapter = new detailListAdapter(lhValue,detailRes);
        detailPlaces.setAdapter(detailAdapter);
        detailPlaces.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        title.setText(getArguments().getString("lhName")+"教室情况");
       }


    class detailListAdapter extends RecyclerView.Adapter<detailListAdapter.detailViewHolder>{

        List<Map> mBeans;
        String lh;
        detailListAdapter(String lh,List<Map> res){
            mBeans = res;
            this.lh = lh;
        }
        @NonNull
        @Override
        public detailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.dynamic_emptyclassroom_detail,viewGroup,false);
            return new detailViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull detailViewHolder detailViewHolder, final int i) {
            detailViewHolder.name.setText((CharSequence) mBeans.get(i).get("name"));
            detailViewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.startEmptyClassroomDetailActivity(getActivity(), String.valueOf(mBeans.get(i).get("name")), TimetableCore.getInstance(HContext).getCurrentCurriculum().getCurriculumCode(), lh, String.valueOf(mBeans.get(i).get("value")));
                }
            });
            if((mBeans.get(i).get("available")!=null&&(Boolean)mBeans.get(i).get("available"))){
//                detailViewHolder.lamp.setCardBackgroundColor(ContextCompat.getColor(HContext,R.color.green_primary));
//            }else{
                detailViewHolder.lamp.setCardBackgroundColor(ContextCompat.getColor(HContext,R.color.material_background_grey_300));
            }
        }

        @Override
        public int getItemCount() {
            return mBeans.size();
        }

        class detailViewHolder extends RecyclerView.ViewHolder{
            TextView name;
            CardView item;
            CardView lamp;
            public detailViewHolder(@NonNull View itemView) {
                super(itemView);
                item = itemView.findViewById(R.id.detail_item);
                name = itemView.findViewById(R.id.detail_name);
                lamp = itemView.findViewById(R.id.lamp);
            }
        }
    }
    class refreshDetailTask extends AsyncTask {
        String lhValue;
        List<Map> targetListRes;
        detailListAdapter targetAdapter;
        ProgressBar loading;
        RecyclerView targetList;
        String lhName;
        refreshDetailTask(String lhName,String lhValue,List<Map> res,detailListAdapter targetAdapter,ProgressBar load,RecyclerView targ){
            this.lhValue = lhValue;
            targetListRes = res;
            loading = load;
            targetList = targ ;
            this.targetAdapter = targetAdapter;
            this.lhName = lhName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            targetList.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Document page = Jsoup.connect("http://jwts.hitsz.edu.cn:8080/kjscx/queryKjs_wdl")
                        //    .data("pageXnxq",TimetableCore.getInstance(HContext).getCurrentCurriculum().curriculumCode)
                        .data("pageZc1", String.valueOf(TimetableCore.getInstance(HContext).getThisWeekOfTerm())).data("pageZc2", String.valueOf(TimetableCore.getInstance(HContext).getThisWeekOfTerm()))
                        .data("pageXiaoqu","1")
                        .data("pageLhdm",lhValue)
                        .data("pageCddm","")
                        .data("pageSize","100")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
                        .post();
                Element lhs = page.getElementById("pageCddm");
                targetListRes.clear();
                List<Map> tempList = new ArrayList<>();
                for(Element lh:lhs.select("option")){
                    //Log.e(lhValue, String.valueOf(lh));
                    if(TextUtils.isEmpty(lh.attr("value"))) continue;
                    Map m = new HashMap();
                    m.put("name",lh.text());
                    m.put("value",lh.attr("value"));
                    tempList.add(m);
//                    JsonObject jo = new JsonObject();
//                    jo.addProperty("domain",lhValue);
//                    jo.addProperty("telephone","26905040");
//                    jo.addProperty("businesshours","7:00-22:30");
//                    jo.addProperty("function","教室");
//                    jo.addProperty("studyhall",false);
//                    jo.addProperty("company","航天物业");
//                    jo.addProperty("code",lh.attr("value"));
//                    Location l = new Location();
//                    l.setInfos(jo.toString());
//                    l.setAddress(lhName);
//                    l.setImageURL("https://bmob-cdn-26359.bmobpay.com/2019/07/27/fe6aa9854079b0288091fd12c8859cb3.jpg");
//                    l.setPositionIntroduction("教室");
//                    l.setRate(0);
//                    l.setStudentnum(0);
//                    l.setType("classroom");
//                    l.setName(lh.text());
//                    l.save(new SaveListener<String>() {
//                        @Override
//                        public void done(String s, BmobException e) {
//
//                        }
//                    });
                }
                Elements rows = page.getElementsByClass("dataTable").select("tr");
                rows.remove(0);
                rows.remove(0);
                for(Element lh:rows){
                    // Log.e("lh",lh.toString());
                    Elements tds = lh.select("td");
                    Map m = new HashMap();
                    m.put("name",tds.get(0).text());
                    boolean hasMatch = false;
                    for(Map tm:tempList){
                        if(tm.get("name").equals(tds.get(0).text())) {
                            m.put("value",tm.get("value"));
                            hasMatch = true;
                            break;
                        }
                    }
                    if(!hasMatch) m.put("value",tds.get(0).text());
                    int dow = TimetableCore.getDOW(TimetableCore.getNow());
                    int number = pageCourseNumber;
                    int index = (dow-1)*6+(number%2==0?number/2:number/2+1);
                    Log.e("Number", String.valueOf(number));
                    m.put("available",number<0||tds.get(index).getElementsByClass("kjs_icon kjs_icon01").size()==0);
                    targetListRes.add(m);
                }
                //System.out.println(page);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return "error!";
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.e("result:", String.valueOf(detailRes));
            targetList.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            targetAdapter.notifyDataSetChanged();
            targetList.scheduleLayoutAnimation();
        }
    }
}
