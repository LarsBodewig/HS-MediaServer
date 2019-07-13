# Server

## Twitter
<table>
	<tr>
		<th>Function</th>
		<th>Param</th>
		<th>Description</th>
		<th>Default</th>
	<tr>
		<td>User bio:</td>
		<td>/api/{user}/user</td>
		<td>returns a users profile as json</td>
		<td></td>
	</tr>
	<tr>
		<td>Tweets:</td>
		<td>/api/{user}/posts</td>
		<td>returns a users tweets and retweets</td>
		<td></td>
	</tr>
	<tr>
		<td></td>
		<td>?from={id}</td>
		<td>returns tweets and retweets starting at tweet with id or with the newest tweet</td>
		<td>null</td>
	</tr>
	<tr>
		<td></td>
		<td>&to={id}</td>
		<td>returns tweets and retweets recursively until tweet with id is found or the first 30</td>
		<td>null</td>
	</tr>
	<tr>
		<td></td>
		<td>&replies={boolean}</td>
		<td>returns tweets and retweets including or excluding replies</td>
		<td>true</td>
	</tr>
</table>