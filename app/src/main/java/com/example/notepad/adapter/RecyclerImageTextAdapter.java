package com.example.notepad.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.DetailActivity;
import com.example.notepad.R;
import com.example.notepad.vo.RecyclerItemVO;

import java.util.ArrayList;

public class RecyclerImageTextAdapter extends RecyclerView.Adapter<RecyclerImageTextAdapter.ViewHolder> {
    private ArrayList<RecyclerItemVO> mData = null ;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public RecyclerImageTextAdapter(ArrayList<RecyclerItemVO> list) {
        mData = list ;
    }

    public ArrayList<RecyclerItemVO> getItems(){
        return mData;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public RecyclerImageTextAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_item, parent, false) ;
        RecyclerImageTextAdapter.ViewHolder vh = new RecyclerImageTextAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RecyclerImageTextAdapter.ViewHolder holder, int position) {

        RecyclerItemVO item = mData.get(position) ;

        holder.icon.setImageDrawable(item.getIconDrawable()) ;
        holder.title.setText(item.getTitleStr()) ;
        holder.desc.setText(item.getDescStr()) ;
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon ;
        TextView title ;
        TextView desc ;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 아이템 클릭 이벤트 처리.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        // 데이터 리스트로부터 아이템 데이터 참조.
                        RecyclerItemVO item = mData.get(pos) ;

                        String index = item.getIndex();

                        // TODO : use item data.
                        // DetailActivity로 index 값 전달
                        Intent intent = new Intent(v.getContext(), DetailActivity.class);
                        intent.putExtra("key", index);
                        v.getContext().startActivity(intent);

                        // TODO : use item.
                    }
                }
            });

            // 뷰 객체에 대한 참조. (hold strong reference)
            icon = itemView.findViewById(R.id.icon) ;
            title = itemView.findViewById(R.id.title) ;
            desc = itemView.findViewById(R.id.desc) ;
        }
    }
}