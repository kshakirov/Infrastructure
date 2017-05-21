require_relative '../lib/source'

turbo_dns = TurboDns.new


INTERFACES = [
    [:udp, "0.0.0.0", 5300],
    [:tcp, "0.0.0.0", 5300],
]

IN = Resolv::DNS::Resource::IN

# Use upstream DNS for name resolution.
UPSTREAM = RubyDNS::Resolver.new([[:udp, "127.0.0.1", 53], [:tcp, "127.0.0.1", 53]])

# Start the RubyDNS server
RubyDNS::run_server(:listen => INTERFACES) do
  
  match(turbo_dns.pattern, IN::A) do |transaction|
    address = turbo_dns.return_address(transaction.query.question.first.first.to_s)
    if not address.nil?
      transaction.respond!(address)
    else
      otherwise do |transaction|
        transaction.passthrough!(UPSTREAM)
      end
    end
  end

  # Default DNS handler
  otherwise do |transaction|
    transaction.passthrough!(UPSTREAM)
  end
end

