--uv:
	select count(*)  
	from ( 
	    select l.*, row_number() over (partition by ip order by create_date) as group_idx  
	    from CMS_SITE_CHART_MAIN l where l.create_date>=sysdate
	) s
	where s.group_idx = 1


--pv:
--	1.
	select to_char(t.create_date, 'Hh24') as hour,
	       to_char(t.create_date, 'YYYY-MM-DD') as day,
	       count(id) as id
	  from CMS_SITE_CHART_MAIN t
	 where t.create_date >= to_date('2016-02-20', 'YYYY-MM-DD')
	 group by to_char(t.create_date, 'YYYY-MM-DD'),
	          to_char(t.create_date, 'Hh24')
	 order by hour, day
	 
--	2. 
	select count(*)
	  from CMS_SITE_CHART_MAIN t
	 where t.create_date >=to_date(to_char(sysdate,'YYYY-MM-DD HH24'), 'YYYY-MM-DD HH24')

--ip:
	select to_char(t.create_date, 'Hh24') as hour,
	       to_char(t.create_date, 'YYYY-MM-DD') as day,
	       count(distinct(ip)) as ip
	  from CMS_SITE_CHART_MAIN t
	 where t.create_date >= to_date('2016-02-20', 'YYYY-MM-DD')
	 group by to_char(t.create_date, 'YYYY-MM-DD'),
	          to_char(t.create_date, 'Hh24')
	 order by hour, day
	 
--nuv:
	select count(*)
	from ( 
	    select l.*, row_number() over (partition by ip order by create_date) as group_idx  
	    from CMS_SITE_CHART_MAIN l
	) s
	where s.group_idx = 1 and s.create_date>sysdate
