package com.example.redisinspring;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    private BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    // @Cacheable : CacheAside 전략 쓰겠다.
    // CacheAside 전략 : 일단 redis 캐시 먼저 뒤지고, 없으면 내부 로직 진행, return 값을 redis에 저장.
    // 키값 - boards:pages:2:size:10 - boards 에서 page가 2이고, size가 10개인 데이터
    @Cacheable(cacheNames = "getBoards", key = "'boards:pages:' + #page + ':size' + #size", cacheManager = "boardCacheManager")
    public List<Board> getBoards(int page, int size) {
        // Cache Miss 가 발생했을 때 진행되는 로직
        // redis에 없으니까 boardRepository(DB) 뒤지는거임
        Pageable pageable = PageRequest.of(page -1, size);
        Page<Board> pageOfBoards = boardRepository.findAllByOrderByCreatedAtDesc(pageable);
        return pageOfBoards.getContent();
    }
}
