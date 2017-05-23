require_relative '../lib/source'

client = TurboInternational::Client.new
client.query ARGV[0]