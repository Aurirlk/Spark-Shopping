/**
 * 商品分享工具
 */

/**
 * 生成分享文案
 */
export function getShareText(product) {
  return {
    title: product.name || '好物推荐',
    desc: product.description || '发现一个很棒的商品！',
    url: `${window.location.origin}/product/${product.id}`,
    price: product.price || 0,
  }
}

/**
 * 复制分享链接
 */
export async function copyShareLink(product) {
  const url = `${window.location.origin}/product/${product.id}`
  try {
    await navigator.clipboard.writeText(url)
    return true
  } catch {
    // fallback
    const ta = document.createElement('textarea')
    ta.value = url; document.body.appendChild(ta); ta.select(); document.execCommand('copy')
    document.body.removeChild(ta)
    return true
  }
}

/**
 * 生成分享卡片文本（用于微信等）
 */
export function getShareCard(product) {
  return `「${product.name}」¥${product.price}\n${product.description || ''}\n${window.location.origin}/product/${product.id}`
}
