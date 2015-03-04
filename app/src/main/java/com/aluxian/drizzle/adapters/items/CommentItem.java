package com.aluxian.drizzle.adapters.items;

import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.UserActivity;
import com.aluxian.drizzle.adapters.multi.MultiTypeItemType;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeBaseItem;
import com.aluxian.drizzle.adapters.multi.items.MultiTypeStyleableItem;
import com.aluxian.drizzle.api.models.Comment;
import com.aluxian.drizzle.utils.CountableInterpolator;
import com.aluxian.drizzle.utils.LocaleManager;
import com.aluxian.drizzle.utils.UberSwatch;
import com.aluxian.drizzle.utils.transformations.CircularTransformation;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CommentItem extends MultiTypeStyleableItem<CommentItem.ViewHolder> {

    /** The {@link com.aluxian.drizzle.adapters.multi.MultiTypeItemType} of this item. */
    public static final MultiTypeItemType<ViewHolder> ITEM_TYPE = new MultiTypeItemType<>(CommentItem.class,
            ViewHolder.class, R.layout.item_comment);

    private final Comment comment;

    public CommentItem(Comment comment) {
        this.comment = comment;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.bg_light);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        View.OnClickListener authorClickListener = (v) -> {
            Intent intent = new Intent(holder.context, UserActivity.class);
            intent.putExtra(UserActivity.EXTRA_USER_DATA, comment.user.toJson());
            holder.context.startActivity(intent);
        };

        holder.avatar.setOnClickListener(authorClickListener);
        holder.author.setOnClickListener(authorClickListener);

        Picasso.with(holder.context)
                .load(comment.user.avatarUrl)
                .transform(new CircularTransformation())
                .placeholder(R.drawable.round_placeholder)
                .into(holder.avatar);

        LocaleManager.set(Locale.US);
        CharSequence time = DateUtils.getRelativeTimeSpanString(comment.createdAt.getTime());
        String likes = "";
        LocaleManager.reset();

        if (comment.likesCount > 0) {
            CountableInterpolator countableInterpolator = new CountableInterpolator(holder.context);
            likes = ", " + countableInterpolator.apply(comment.likesCount, R.string.stats_likes, R.string.stats_like);
        }

        holder.author.setText(comment.user.name);
        holder.body.setText(Html.fromHtml(comment.body));
        holder.body.setMovementMethod(LinkMovementMethod.getInstance());
        holder.footer.setText(time + likes);
    }

    @Override
    public int getId(int position) {
        return comment.id;
    }

    @Override
    protected void onSetStyle(ViewHolder holder, UberSwatch swatch) {
        holder.body.setLinkTextColor(swatch.rgb);
    }

    public static class ViewHolder extends MultiTypeBaseItem.ViewHolder {

        @InjectView(R.id.avatar) ImageView avatar;
        @InjectView(R.id.title) TextView author;
        @InjectView(R.id.description) TextView body;
        @InjectView(R.id.footer) TextView footer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

}