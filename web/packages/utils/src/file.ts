type FileResponse = {
  data: BlobPart;
  headers: Record<string, string>;
};

/**
 * 根据后台接口文件流下载
 *
 * @param response 后端返回的文件流和响应头。
 * @example
 * ```ts
 * exportFile({
 *   data: blobPart,
 *   headers: {
 *     "content-disposition": "attachment; filename=demo.xlsx",
 *     "content-type": "application/vnd.ms-excel",
 *   },
 * });
 * ```
 */
export function exportFile(response: FileResponse) {
  const disposition = response.headers["content-disposition"];
  const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
  const matches = filenameRegex.exec(disposition);
  const filename = (matches && decodeURIComponent(matches[1])) || "文件";
  const blob = new Blob([response.data], { type: response.headers["content-type"] });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  URL.revokeObjectURL(url);
  document.body.removeChild(link);
}
