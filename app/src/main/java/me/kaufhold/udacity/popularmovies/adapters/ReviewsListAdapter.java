package me.kaufhold.udacity.popularmovies.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devs.readmoreoption.ReadMoreOption;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.kaufhold.udacity.popularmovies.R;
import me.kaufhold.udacity.popularmovies.model.Review;
import me.kaufhold.udacity.popularmovies.model.ReviewsResultPage;

public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.ReviewsListHolder>{
    private Context context;
    private List<Review> reviews = new LinkedList<>();
    private int currentPage = -1;
    private int maxPage = Integer.MAX_VALUE;
    private PageLoader pageLoader;

    public ReviewsListAdapter(Context applicationContext) {
        this.context = applicationContext;
    }

    public void init(List<Review> reviews, int currentPage, int maxPage, PageLoader pageLoader) {
        this.reviews = reviews;
        this.currentPage = currentPage;
        this.maxPage = maxPage;
        this.pageLoader = pageLoader;
    }

    @NonNull
    @Override
    public ReviewsListAdapter.ReviewsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View review_item_view = inflater.inflate(R.layout.review_item, parent, false);
        return new ReviewsListHolder(review_item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsListAdapter.ReviewsListHolder holder, int position) {
        if(reviews == null){
            return;
        }
        if(position == reviews.size() - 1 && currentPage > 0 && currentPage + 1 <= maxPage) {
            pageLoader.loadNextPage(currentPage);
        }
        Review review = reviews.get(position);
        if(review == null){
            return;
        }
        holder.setReview(review);
        if(review.getAuthor() != null){
            TextView author_tv = holder.review_author.findViewById(R.id.review_author);
            author_tv.setText(review.getAuthor());
        }
        if(review.getContent() != null) {
            TextView context_tv = holder.review_content.findViewById(R.id.review_context);
            setTextAndAddReadMoreOption(context_tv, review);
        }
    }

    private void setTextAndAddReadMoreOption(TextView context_tv, Review review) {
        ReadMoreOption readMoreOption = new ReadMoreOption.Builder(context)
                .textLength(300, ReadMoreOption.TYPE_CHARACTER)
                .moreLabel(" read more")
                .lessLabel(" less")
                .moreLabelColor(Color.parseColor("#76cf96"))
                .lessLabelColor(Color.parseColor("#76cf96"))
                .labelUnderLine(true)
                .expandAnimation(true)
                .build();
        readMoreOption.addReadMoreTo(context_tv, review.getContent());
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(reviews != null) {
            count = this.reviews.size();
        }
        return count;
    }

    public ArrayList<Review> createReviewsArray() {
        return new ArrayList<>(reviews);
    }

    public void addReviewPage(ReviewsResultPage page) {
        currentPage = page.getPage();
        maxPage = page.getTotalPages();
        addReviews(page.getResults());
    }

    public static class ReviewsListHolder extends RecyclerView.ViewHolder {
        private Review review;
        protected TextView review_author;
        protected TextView review_content;

        public ReviewsListHolder(@NonNull View item_view) {
            super(item_view);
            review_author = item_view.findViewById(R.id.review_author);
            review_content = item_view.findViewById(R.id.review_context);
            if(this.review != null) {
                if(review.getAuthor() != null){
                    review_author.setText(review.getAuthor());
                }
                if(review.getContent() != null) {
                    review_content.setText(review.getContent());
                }
            }
        }

        public void setReview(Review review) {
            this.review = review;
        }
    }

    public void addReviews(List<Review> reviews) {
        this.reviews.addAll(reviews);
        notifyDataSetChanged();
    }
}
