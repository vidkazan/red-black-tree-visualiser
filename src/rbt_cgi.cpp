#include "treeApi.hpp"
using namespace std;

int main() {
    char buf[1000];
    bzero(buf,1000);
    ssize_t ret = read(0,buf,999);
    if(ret > -1 && strlen(buf)) {
        treeApi_std<int,int> std(buf);
        std.makeJsonFromTree("tree.json");
        treeApi_ft<int,int> ft(buf);
        ft.makeJsonFromTree("tree_ft.json");
    }
    return 0;
}
