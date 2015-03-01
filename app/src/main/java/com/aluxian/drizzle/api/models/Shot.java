package com.aluxian.drizzle.api.models;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import com.aluxian.drizzle.R;
import com.aluxian.drizzle.activities.TeamActivity;
import com.aluxian.drizzle.activities.UserActivity;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public final class Shot extends Model {

    public final int id;
    public final String title;
    public final String description;
    public final Images images;
    public final int viewsCount;
    public final int likesCount;
    public final int commentsCount;
    public final int attachmentsCount;
    public final int reboundsCount;
    public final int bucketsCount;
    public final Date createdAt;
    public final Date updatedAt;
    public final String htmlUrl;
    public final List<String> tags;
    public final User user;
    public final Team team;

    public Shot(int id, String title, String description, Images images, int viewsCount, int likesCount, int commentsCount,
                int attachmentsCount, int reboundsCount, int bucketsCount, Date createdAt, Date updatedAt, String htmlUrl,
                List<String> tags, User user, Team team) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.images = images;
        this.viewsCount = viewsCount;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.attachmentsCount = attachmentsCount;
        this.reboundsCount = reboundsCount;
        this.bucketsCount = bucketsCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.htmlUrl = htmlUrl;
        this.tags = tags;
        this.user = user;
        this.team = team;
    }

    /**
     * Generates a {@code SpannableString} with clickable segments..
     *
     * @param context A context required for starting intents.
     * @return A "by {@link #user} for {@link #team}" string.
     */
    public SpannableString generateAuthorDescription(Context context) {
        String wordBy = context.getResources().getString(R.string.word_by) + " ";
        String wordFor = " " + context.getResources().getString(R.string.word_for) + " ";

        ClickableSpan userClickable = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra(UserActivity.EXTRA_USER_DATA, user.toJson());
                context.startActivity(intent);
            }
        };

        SpannableString spannable = new SpannableString(wordBy + user.name + (team != null ? wordFor + team.name : ""));
        spannable.setSpan(userClickable, wordBy.length(), wordBy.length() + user.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (team != null) {
            ClickableSpan teamClickable = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent intent = new Intent(context, TeamActivity.class);
                    intent.putExtra(TeamActivity.EXTRA_TEAM_DATA, team.toJson());
                    context.startActivity(intent);
                }
            };

            int start = wordBy.length() + user.name.length() + wordFor.length();
            spannable.setSpan(teamClickable, start, start + team.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannable;
    }

    /**
     * @return Whether this shot is a GIF image.
     */
    public boolean isGif() {
        return images.normal.toLowerCase().endsWith(".gif");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shot shot = (Shot) o;
        return id == shot.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public static final class Images {

        public final String hidpi;
        public final String normal;
        public final String teaser;

        public Images(String hidpi, String normal, String teaser) {
            this.hidpi = hidpi;
            this.normal = normal;
            this.teaser = teaser;
        }

        /**
         * @return The largest version of the images which is available.
         */
        public String largest() {
            if (hidpi != null) {
                return hidpi;
            }

            return normal;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Images images = (Images) o;

            return Objects.equals(hidpi, images.hidpi)
                    && Objects.equals(normal, images.normal)
                    && Objects.equals(teaser, images.teaser);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hidpi, normal, teaser);
        }

    }

}
