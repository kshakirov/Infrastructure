module TurboInternational
  class Client


    def initialize
      @addr_pattern = '10.8.'
      @dns_server = '10.8.0.4'
      @dns_port = 53
    end

    def get_full_name name
      "#{name}.cluster.turbointernational.com"
    end

    def get_vpn_ip
      addr_infos = Socket.ip_address_list
      address = addr_infos.find do |addr|
        addr.ip_address.include? @addr_pattern
      end
      address.ip_address
    end



    def query name
       p system("dig @localhost -p 5300 \"#{get_full_name(name)}\"")
    end

    def set_dns name
      p system("dig @localhost -p 5300 \"set_dns:#{get_full_name(name)}:#{get_vpn_ip}\"")
    end

  end
end
