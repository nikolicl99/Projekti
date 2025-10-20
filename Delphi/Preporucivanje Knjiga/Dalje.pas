unit Dalje;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FMX.Controls.Presentation, FMX.StdCtrls, FMX.Memo.Types, FMX.ScrollBox,
  FMX.Memo;

type
  TfrmDalje = class(TForm)
    Forma: TPanel;
    StyleBook: TStyleBook;
    StyleBook1: TStyleBook;
    Memo1: TMemo;
    Biraj: TButton;
    Izlaz: TButton;
    procedure IzlazClick(Sender: TObject);
    procedure BirajClick(Sender: TObject);
    procedure FormActivate(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmDalje: TfrmDalje;

implementation

{$R *.fmx}
uses PocetnaStrana, Prikaz, Vise, Main;

procedure TfrmDalje.BirajClick(Sender: TObject);
begin
frmPocetnaStrana.Show;
frmMain.CurrentBookID := 0;
Self.Hide;
frmPrikaz.Hide;
frmVise.Hide;
end;

procedure TfrmDalje.FormActivate(Sender: TObject);
begin
Left := Round((Screen.Width - Width) / 2);
  Top := Round((Screen.Height - Height) / 2);
end;

procedure TfrmDalje.IzlazClick(Sender: TObject);
begin
Application.Terminate;
end;

end.
