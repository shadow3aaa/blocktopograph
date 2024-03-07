package io.vn.nguyenduck.blocktopograph;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static io.vn.nguyenduck.blocktopograph.Constants.*;
import static io.vn.nguyenduck.blocktopograph.Logger.LOGGER;
import static io.vn.nguyenduck.blocktopograph.TranslationUtils.translateGamemode;

public class WorldListAdapter extends RecyclerView.Adapter<WorldListAdapter.WorldItem> {
    private DocumentFile rootFolder;

    private final ArrayList<WorldLevelData> worldLevelData = new ArrayList<>();

    public static class WorldItem extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView icon;
        public final TextView name;
        public final TextView gamemode;
        public final TextView experimental;
        public final TextView last_play;
        public final TextView size;

        public WorldItem(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            icon = view.findViewById(R.id.world_item_icon);
            name = view.findViewById(R.id.world_item_name);
            gamemode = view.findViewById(R.id.world_item_gamemode);
            experimental = view.findViewById(R.id.world_item_experimental);
            last_play = view.findViewById(R.id.world_item_last_play);
            size = view.findViewById(R.id.world_item_size);
        }
    }

    @NonNull
    @Override
    public WorldItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.world_item, viewGroup, false);
        return new WorldItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorldItem viewHolder, int i) {
        WorldLevelData data = worldLevelData.get(i);
        Bundle bundle = data.dataBundle;
        LOGGER.info(bundle.toString());

        String worldName = data.rawWorldName;

        viewHolder.name.setText(worldName.length() > 20 ? worldName.substring(0, 20) + "..." : worldName);
        viewHolder.experimental.setVisibility(bundle.getBundle("experiments") == null ? View.GONE : View.VISIBLE);
        viewHolder.gamemode.setText(translateGamemode(bundle.getInt("GameType")));
        viewHolder.size.setText(data.worldSizeFormated);
        viewHolder.last_play.setText(
                DateFormat.getDateFormat(viewHolder.view.getContext())
                        .format(bundle.getLong("LastPlayed") * 1000));

        if (data.worldIconUri != null) {
            viewHolder.icon.setImageURI(data.worldIconUri);
        } else {
            if (bundle.getBundle("experiments") == null) {
                viewHolder.icon.setImageResource(R.drawable.world_demo_screen_big);
            } else {
                viewHolder.icon.setImageResource(R.drawable.world_demo_screen_big_grayscale);
            }
        }
    }

    @Override
    public int getItemCount() {
        return worldLevelData.size();
    }

    public void initAdapter(DocumentFile appDataFolder) {
        if (rootFolder == null) rootFolder = appDataFolder;
        DocumentFile f = DocumentUtils.getFileFromPath(rootFolder, new String[]{
                "files",
                "games",
                COM_MOJANG_FOLDER,
                WORLDS_FOLDER});
        if (f != null) {
            Arrays.stream(f.listFiles()).forEach(file -> {
                worldLevelData.add(new WorldLevelData(file));
            });
        }
    }
}
