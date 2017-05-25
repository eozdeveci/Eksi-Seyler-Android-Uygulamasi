
<?php


	include('simple_html_dom.php');	

	$kategori=$_POST["kategori"]; 
	//$kategori = "spor";
	$url = "https://seyler.eksisozluk.com/kategori/" . $kategori;
	
	$html = file_get_html($url);

	$i = 0;
	$myarray = array();
	$src = "data-src";
	foreach($html->find('//a//img ') as $data)
	{

		if($data->style == false && $data->$src == false && $data->alt == false)
		{
			continue;
		}
		else
		{
			$output[$i]['id']= $i+1;
   			$output[$i]['style']= $data->style;
   			$output[$i]['src']= $data->$src;
   			$output[$i]['alt']= $data->alt;

   			$i++;
		}
	}

	$i=0;
	foreach($html->find('//a') as $data)
	{

		if(strpos($data->href,'http://') !== false)
		{
			if ($i>0)
			{
				if((strpos($data->href, 'eksi-seyler-de-yayinlanan-her-icerik-icin-bir-fidan-dikiyoruz') !== false))
				{

				}
				else if($output[$i-1]['domain'] != $data->href)
				{
					$output[$i]['domain']= $data->href;
	   				$i++;
				}
			}
			else
			{
				$output[$i]['domain']= $data->href;
	   			$i++;
			}
			
		}
		else
		{
			continue;
		}
	}

	$i=4;
	foreach($html->find('.meta-stats') as $data){

   		$output[$i]['stats']= $data-> plaintext;
   		$i++;
	}

	$i=4;
	foreach($html->find('.meta-category') as $data){

   		$output[$i]['category']= $data-> plaintext;
   		$i++;
	}



	print_r (json_encode ($output,JSON_UNESCAPED_UNICODE));

?>