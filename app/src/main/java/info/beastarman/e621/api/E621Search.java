package info.beastarman.e621.api;

import java.util.ArrayList;

public class E621Search implements E621SearchInterface
{
	private static final long serialVersionUID = 2951933484510381506L;
	
	public ArrayList<E621Image> images;
	public int offset;
	public int count;
	public int results_per_page;
	
	public E621Search()
	{
		this.images = new ArrayList<E621Image>();
		this.offset=0;
		this.count=0;
		this.results_per_page=1;
	}
	
	public E621Search(ArrayList<E621Image> images, int offset, int count, int results_per_page)
	{
		this.images = images;
		this.offset = offset;
		this.count = count;
		this.results_per_page = results_per_page;

		impossibleCount();
	}

	private void impossibleCount()
	{
		if(count == 0 && images.size() > 0)
		{
			count = images.size();
		}
	}
	
	@Override
	public boolean has_prev_page()
	{
		return current_page(1) > 1;
	}
	
	@Override
	public boolean has_next_page()
	{
		return current_page(1) < total_pages();
	}
	
	@Override
	public int current_page()
	{
		return current_page(0);
	}
	
	@Override
	public int current_page(int diff)
	{
		return (int) Math.floor(((double)offset)/((double)results_per_page)) + diff;
	}
	
	@Override
	public int total_pages()
	{
		return (int) Math.ceil(((double)count)/((double)results_per_page));
	}
}
