import request from '@/utils/request'

// 查询礼品卡列表
export function listGiftCard(query) {
  return request({
    url: '/GiftCard/GiftCard/list',
    method: 'get',
    params: query
  })
}

// 查询礼品卡详细
export function getGiftCard(id) {
  return request({
    url: '/GiftCard/GiftCard/' + id,
    method: 'get'
  })
}

// 新增礼品卡
export function addGiftCard(data) {
  return request({
    url: '/GiftCard/GiftCard',
    method: 'post',
    data: data
  })
}

// 修改礼品卡
export function updateGiftCard(data) {
  return request({
    url: '/GiftCard/GiftCard',
    method: 'put',
    data: data
  })
}

// 删除礼品卡
export function delGiftCard(id) {
  return request({
    url: '/GiftCard/GiftCard/' + id,
    method: 'delete'
  })
}
