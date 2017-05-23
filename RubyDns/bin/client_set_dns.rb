require_relative '../lib/source'

client = TurboInternational::Client.new
client.set_dns ARGV[0]