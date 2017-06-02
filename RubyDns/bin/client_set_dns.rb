require_relative '../lib/source'

client = TurboInternational::Client.new
client.set_dns_lazy ARGV[0], ARGV[1]
