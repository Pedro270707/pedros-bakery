package net.pedroricardo.client.render.item;

import java.util.Set;

public record CookieFaces(Set<ShapedCookieItemRenderer.Face> lightBorder, Set<ShapedCookieItemRenderer.Face> darkBorder, Set<ShapedCookieItemRenderer.Face> lightInner, Set<ShapedCookieItemRenderer.Face> darkInner) {
}
