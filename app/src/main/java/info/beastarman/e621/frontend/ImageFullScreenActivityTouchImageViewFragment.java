package info.beastarman.e621.frontend;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.IOException;

import info.beastarman.e621.R;
import info.beastarman.e621.api.E621Image;
import info.beastarman.e621.backend.Pair;
import info.beastarman.e621.middleware.E621Middleware;
import info.beastarman.e621.middleware.ImageLoadRunnable;
import info.beastarman.e621.middleware.ImageNavigator;
import info.beastarman.e621.middleware.ImageNavilagorLazyRelative;
import info.beastarman.e621.middleware.TouchImageViewHandler;
import info.beastarman.e621.views.TouchImageView;

public class ImageFullScreenActivityTouchImageViewFragment extends Fragment
{
	public static String LAZY_POSITION = "lazy";
	ImageNavigator image;

	public static ImageFullScreenActivityTouchImageViewFragment fromImageNavigator(ImageNavilagorLazyRelative navigator)
	{
		ImageFullScreenActivityTouchImageViewFragment ret = new ImageFullScreenActivityTouchImageViewFragment();

		Bundle bundle = new Bundle();

		bundle.putSerializable(LAZY_POSITION, navigator);

		ret.setArguments(bundle);

		return ret;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.image_full_screen_static_image, container, false);

		final TouchImageView t = (TouchImageView) rl.findViewById(R.id.touchImageView);
		final ProgressBar p = (ProgressBar) rl.findViewById(R.id.progressBar);

		t.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Activity a = getActivity();

				if(a instanceof ImageFullScreenActivity)
				{
					((ImageFullScreenActivity)a).toggleVisibility();
				}
			}
		});

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				image = ((ImageNavilagorLazyRelative) getArguments().getSerializable(LAZY_POSITION)).getImageNavigator();
				getImage(t,p);
			}
		}).start();

		return rl;
	}

	private void getImage(final TouchImageView t, final ProgressBar p)
	{
		try
		{
			final E621Image img = E621Middleware.getInstance().post__show(image.getId());

			final Pair<Integer,Integer> size = img.getSize(E621Middleware.getInstance().getFileDownloadSize());

			final float scale = Math.max(1, Math.max(size.left / 2048f, size.right / 2048f));

			getActivity().runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					TouchImageViewHandler handler = new TouchImageViewHandler(t, p, (int) (size.left / scale), (int) (size.right / scale));

					new Thread(new ImageLoadRunnable(handler, img, E621Middleware.getInstance(), E621Image.FULL)).start();
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}