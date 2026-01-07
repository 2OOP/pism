$client = New-Object System.Net.Sockets.TcpClient("localhost", 6666)
$stream = $client.GetStream()
$writer = New-Object System.IO.StreamWriter($stream)
$reader = New-Object System.IO.StreamReader($stream)

$client2 = New-Object System.Net.Sockets.TcpClient("localhost", 6666)
$stream2 = $client2.GetStream()
$writer2 = New-Object System.IO.StreamWriter($stream2)
$reader2 = New-Object System.IO.StreamReader($stream2)

$writer.WriteLine("login john")
$writer.Flush()
$reader.ReadLine()

$writer2.WriteLine("login hendrik")
$writer2.Flush()
$reader2.ReadLine()