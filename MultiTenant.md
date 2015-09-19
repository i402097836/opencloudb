multi tenent  support by opencloudb can be implemented as following:
  * every tenent create a account named tenantId
  * every user table automatic add a column named tenantId
  * tanantId can be used a a partition field
  * every sql sent by user Must auto add tenentId by opencloudb


for physical isolation ,we can sharding database with tenent id ,different database to storage different users tables which is totally transparent
current application doesn't need to change code ,only deploy multi instance using different tenent database account .