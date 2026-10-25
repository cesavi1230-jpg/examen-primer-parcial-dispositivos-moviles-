package com.ucenm.examen1;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private Context context;
    private List<Contacto> contactoList;
    private OnContactActionListener listener;

    public interface OnContactActionListener {
        void onCallClick(Contacto contacto);
        void onShareClick(Contacto contacto);
        void onEditClick(Contacto contacto);
        void onDeleteClick(Contacto contacto);
        void onImageClick(Contacto contacto);
    }

    public ContactAdapter(Context context, List<Contacto> contactoList, OnContactActionListener listener) {
        this.context = context;
        this.contactoList = contactoList;
        this.listener = listener;
    }

    public void updateList(List<Contacto> newList) {
        this.contactoList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contacto contacto = contactoList.get(position);

        holder.tvNombre.setText(contacto.getNombre());
        holder.tvTelefono.setText(contacto.getPais() + " " + contacto.getTelefono());
        holder.tvNota.setText(contacto.getNota() != null && !contacto.getNota().isEmpty() ? contacto.getNota() : "Sin nota");

        if (contacto.getImagenUri() != null && !contacto.getImagenUri().isEmpty()) {
            try {
                holder.ivItemPhoto.setImageURI(Uri.parse(contacto.getImagenUri()));
            } catch (Exception e) {
                holder.ivItemPhoto.setImageResource(android.R.drawable.ic_menu_camera);
            }
        } else {
            holder.ivItemPhoto.setImageResource(android.R.drawable.ic_menu_camera);
        }

        holder.btnCall.setOnClickListener(v -> {
            if (listener != null) listener.onCallClick(contacto);
        });

        holder.btnShare.setOnClickListener(v -> {
            if (listener != null) listener.onShareClick(contacto);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(contacto);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(contacto);
        });

        holder.ivItemPhoto.setOnClickListener(v -> {
            if (listener != null) listener.onImageClick(contacto);
        });
    }

    @Override
    public int getItemCount() {
        return contactoList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemPhoto;
        TextView tvNombre, tvTelefono, tvNota;
        Button btnCall, btnShare, btnEdit, btnDelete;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemPhoto = itemView.findViewById(R.id.ivItemPhoto);
            tvNombre = itemView.findViewById(R.id.tvItemNombre);
            tvTelefono = itemView.findViewById(R.id.tvItemTelefono);
            tvNota = itemView.findViewById(R.id.tvItemNota);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
