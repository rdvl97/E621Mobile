package info.beastarman.e621.middleware;

import info.beastarman.e621.api.E621Image;
import info.beastarman.e621.api.E621Search;

import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;

public class OnlineImageNavigator implements ImageNavigator
{
	E621Image img;
	int position;
	String query;
	ArrayList<E621Image> cache;
	int cache_offset;
	int total;
	
	public OnlineImageNavigator(E621Image img, int position, String query, ArrayList<E621Image> cache, int cache_offset, int total)
	{
		this.img = img;
		this.position = position;
		this.query = query;
		this.cache = cache;
		this.cache_offset = cache_offset;
		this.total = total;
	}
	
	public OnlineImageNavigator(E621Image img, int position, String query, E621Search search)
	{
		this.img = img;
		this.position = position;
		this.query = query;
		this.cache = new ArrayList<E621Image>(search.images);
		this.cache_offset = search.offset;
		this.total = search.count;
	}
	
	@Override
	public ImageNavigator next()
	{
		int new_position = position+1;
		
		if(new_position >= total)
		{
			return null;
		}

		Log.d("Msg","1");
		
		while(!(new_position < cache_offset + cache.size()))
		{
			Log.d("Msg","2");
			
			int page_append = (int) Math.floor(((double)cache_offset + cache.size())/20);
			int slice_from = (cache_offset + cache.size()) % 20;
			
			ArrayList<E621Image> results;
			
			try {
				results = E621Middleware.getInstance().post__index(query, page_append, 20).images;
			} catch (IOException e) {
				return null;
			}
			
			Log.d("Msg","2a");
			
			if(results == null)
			{
				return null;
			}
			
			Log.d("Msg","2b");
			
			if(results.size() <= slice_from)
			{
				return null;
			}
			
			results.subList(0,slice_from).clear();
			
			cache.addAll(results);
			
			Log.d("Msg","2c");
		}
		
		Log.d("Msg","3");
		
		if(new_position < cache_offset + cache.size())
		{
			return new OnlineImageNavigator(cache.get(new_position - cache_offset),new_position,query,cache,cache_offset,total);
		}
		
		return null;
	}

	@Override
	public ImageNavigator prev()
	{
		int new_position = position-1;
		
		if(new_position < 0)
		{
			return null;
		}
		
		while(!(new_position >= cache_offset))
		{
			int page_append = (int) Math.floor(((double)cache_offset -1)/20);
			int slice_to = ((cache_offset - 1) % 20) + 1;
			
			ArrayList<E621Image> results;
			
			try {
				results = E621Middleware.getInstance().post__index(query, page_append, 20).images;
			} catch (IOException e) {
				return null;
			}
			
			if(results == null)
			{
				return null;
			}
			
			if(results.size() < slice_to)
			{
				return null;
			}
			
			results.subList(slice_to,results.size()).clear();
			
			cache.addAll(0,results);
			
			cache_offset -= results.size();
		}
		
		if(new_position >= cache_offset)
		{
			return new OnlineImageNavigator(cache.get(new_position - cache_offset),new_position,query,cache,cache_offset,total);
		}
		
		return null;
	}

	@Override
	public String getId() {
		return img.id + "." + img.file_ext;
	}

	public String toString()
	{
		return getId();
	}
}