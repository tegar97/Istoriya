package com.tegar.istoriya

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.tegar.istoriya.data.local.room.StoryDao
import com.tegar.istoriya.data.local.room.StoryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.ArrayList

internal class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    private lateinit var dao : StoryDao

    override fun onCreate() {
        dao = StoryDatabase.getInstance(mContext.applicationContext).StoryDao()


    }

    override fun onDataSetChanged() {
        val tokenIdentifier = Binder.clearCallingIdentity()
        runBlocking (Dispatchers.IO) {
            try {
                dao.getLatestStory().map {
                    val bitmap = try {
                        Glide.with(mContext)
                            .asBitmap()
                            .load(it.photoUrl)
                            .submit()
                            .get()
                    } catch (e: Exception) {
                        BitmapFactory.decodeResource(mContext.resources, R.drawable.ic_place_holder)
                    }
                    mWidgetItems.add(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Binder.restoreCallingIdentity(tokenIdentifier)
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.story_widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            ListStoryWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}