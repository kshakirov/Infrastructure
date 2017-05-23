module TurboInternational
  class TurboDns
    attr_reader :interfaces, :cluster_pattern, :upstream, :in, :set_pattern

    def initialize
      @cluster_pattern = /cluster/
      @set_pattern = /set_dns/
      @resolve_hash = {}

    end

    def normalize addr
      normalized = addr.chop
      normalized.chomp('.')
    end

    def return_address addr
        normalized = normalize addr
      if @resolve_hash.key? normalized
        @resolve_hash[normalized]
      end
    end

    def set_dns addr
      parts = addr.split(':')
      @resolve_hash[parts[1]] = parts[2].chomp('.')
      "#{@resolve_hash[parts[1]]}"
    end


  end
end